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
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
        if (task.getParentTaskId() == null){
            List<Task> subtasks = taskRepository.findSubtasksByParentId(taskId);
            for (Task t : subtasks){
                t.setDone(newStatus);
                taskRepository.updateIsDoneInTaskById(t);
            }}
        task.setDone(newStatus);
        return taskRepository.updateIsDoneInTaskById(task);

    }

    public List<TimeEntry> getTimeEntriesByTaskId(int taskId) {
        return taskRepository.findTimeEntriesByTaskId(taskId);
    }

    public int getDoneSubtasks(List<Task> tasks){
        int count = 0;
        for (Task t : tasks){
            if (t.isDone() == true && t.getParentTaskId() != null){
                count ++;
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


    public boolean hasChildren(int taskId){
        return taskRepository.taskHasChildren(taskId);
    }
    public List<Task> getTasksByParentId(int parentId){
        return taskRepository.findSubtasksByParentId(parentId);
    }
}
