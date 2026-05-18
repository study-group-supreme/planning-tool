package planningtool.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
    }

    public Task getTaskById(int id) {
        if (id <= 0) {
            throw new BadRequestException("Invalid task id");
        }
        try {
            return taskRepository.findTaskById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Nothing to show for task with id:" + id);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Database error while loading task", e);
        }
    }

    @Transactional
    public Task createTask(Task task) {

        // Check if this task has a parent, and check if the parent itself is not a subtask
        if (task.getParentTaskId() != null) {
            Task parent = taskRepository.findTaskById(task.getParentTaskId());
            if (parent.getParentTaskId() != null){
                throw new BadRequestException("Subtasks cannot have their own subtasks");
            }
        }

        // If user set task to high priority in normal creation, it stays
        if (!task.isHighPriority()) {
            task.setHighPriority(false);
        }
        task.setDone(false);

        // Previous validation rules
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (task.getTitle().length() > 225) {
            throw new BadRequestException("Task title cannot exceed 225 characters");
        }
        if ((task.getDescription() != null) && (task.getDescription().length() > 1080)) {
            throw new BadRequestException("Task description cannot exceed 1080 characters");
        }
        if ((task.getTimeEstimate() != null) && (task.getTimeEstimate().compareTo(new BigDecimal("9999.99")) > 0)) {
            throw new BadRequestException("Time estimate cannot exceed 9999.99 hours");
        }

        try {
            return taskRepository.insertTask(task);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Task creation failed", e);
        }
    }

    @Transactional
    public TimeEntry createTimeEntry(TimeEntry timeEntry) {
        if (timeEntry.getTaskId() <= 0) {
            throw new BadRequestException("Invalid task id");
        }


        BigDecimal timeSpent = timeEntry.getTimeSpent();
        if (timeSpent == null || timeSpent.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Time spent must be a positive number");
        }
        if (timeSpent.compareTo(new BigDecimal("9999.99")) > 0) {
            throw new BadRequestException("Time spent cannot exceed 9999.99 hours");
        }

        try {
            taskRepository.findTaskById(timeEntry.getTaskId());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Nothing to show for task with id:" + timeEntry.getTaskId());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Database error while loading task", e);
        }


        try {
            return taskRepository.insertTimeEntry(timeEntry);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Failed to create time entry", e);
        }
    }

    public void removeTimeEntryById(int timeEntryId) {
        try {
            taskRepository.deleteTimeEntryById(timeEntryId);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Failed to delete time entry", e);
        }
    }

    public Task editTask(Task task) {

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (task.getTitle().length() > 225) {
            throw new BadRequestException("Task title cannot exceed 225 characters");
        }
        if ((task.getDescription() != null) && (task.getDescription().length() > 1080)) {
            throw new BadRequestException("Task description cannot exceed 1080 characters");
        }
        if ((task.getTimeEstimate() != null) && (task.getTimeEstimate().compareTo(new BigDecimal("9999.99")) > 0)) {
            throw new BadRequestException("Time estimate cannot exceed 9999.99 hours");
        }

        try {
            taskRepository.updateTask(task);
            return taskRepository.findTaskById(task.getId());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException(e.getMessage(), e.getCause());
        }
    }


    public void removeTaskById(int id) {
        Task task = taskRepository.findTaskById(id);
        if (task.getParentTaskId() == null) {
            List<Task> tasks = taskRepository.findSubtasksByParentId(id);
            for (Task t : tasks) {
                if (!t.isDone()) {
                    throw new BadRequestException("You cannot delete main task before deleting all subtask or marking them as done");
                }
            }
        }
        taskRepository.deleteTaskById(id);
    }

    //Properly need some validation later
    public Task editTaskIsDoneStatus(int taskId) {
        Task task = taskRepository.findTaskById(taskId);
        boolean newStatus = !task.isDone();
        if (task.getParentTaskId() == null) {
            List<Task> subtasks = taskRepository.findSubtasksByParentId(taskId);
            for (Task t : subtasks) {
                t.setDone(newStatus);
                taskRepository.updateIsDoneInTaskById(t);
            }
        }
        task.setDone(newStatus);
        return taskRepository.updateIsDoneInTaskById(task);

    }

    public List<TimeEntry> getTimeEntriesByTaskId(int taskId) {
        return taskRepository.findTimeEntriesByTaskId(taskId);
    }

    public int getDoneSubtasks(List<Task> tasks) {
        int count = 0;
        for (Task t : tasks) {
            if (t.isDone() == true && t.getParentTaskId() != null) {
                count++;
            }
        }
        return count;
    }

    public int getTotalSubtasks(List<Task> tasks) {
        int count = 0;
        for (Task t : tasks) {
            if (t.getParentTaskId() != null) {
                count++;
            }
        }
        return count;
    }


    public boolean hasChildren(int taskId) {
        return taskRepository.taskHasChildren(taskId);
    }

    public List<Task> getTasksByParentId(int parentId) {
        return taskRepository.findSubtasksByParentId(parentId);
    }

    public Map<Task, int[]> getMainTaskProgress(int projectId) {
        Map<Task, int[]> map = new HashMap<>();
        List<Task> tasks = projectService.getProjectById(projectId).getTasks();
        for (Task t : tasks) {
            if (t.getParentTaskId() == null) { // main task
                List<Task> subtasks = getTasksByParentId(t.getId());
                map.put(t, new int[]{
                        getDoneSubtasks(subtasks),
                        getTotalSubtasks(subtasks)
                });
            }
            //{0} = doneSubtasks
            //{1} = totalSubtasks
        }
        return map;
    }

    public BigDecimal calculateTotalEstimatedTimeForParentTask(int parentTaskId){
        List<Task> subtasks = taskRepository.findSubtasksByParentId(parentTaskId);

        BigDecimal total = BigDecimal.ZERO;
        for (Task task : subtasks){
            if (task.getTimeEstimate() != null){
                total = total.add(task.getTimeEstimate());
            }
        }

        return total;
    }
}
