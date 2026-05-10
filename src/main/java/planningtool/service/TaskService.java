package planningtool.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;

import javax.xml.crypto.Data;
import java.math.BigDecimal;

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
// TODO We need to create some error handling here!
    @Transactional
    public Task createTask(Task task) {
        try {
            return taskRepository.insertTask(task);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Task creation failed", e);
        }
    }

    @Transactional
    public TimeEntry createTimeEntry(TimeEntry timeEntry) {
        if (timeEntry == null) {
            throw new BadRequestException("Time entry cannot be null");
        }
        if (timeEntry.getTaskId() <= 0) {
            throw new BadRequestException("Invalid task id");
        }


        BigDecimal timeSpent = timeEntry.getTimeSpent();
        if (timeSpent == null || timeSpent.compareTo(BigDecimal.ZERO) <=0) {
            throw new BadRequestException("Time spent must be a positive number");
        }

        // TODO: Optionally, a task-exists-check like the try/catch block on line 31 above + a similar check for employee-exists (employeeRepo would need to be imported tho)
//        try {
//            taskRepository.findTaskById(timeEntry.getTaskId());
//        } catch (EmptyResultDataAccessException e) {
//            throw new NotFoundException("Nothing to show for task with id:" + timeEntry.getTaskId());
//        } catch (DataAccessException e) {
//            throw new DatabaseOperationException("Database error while loading task", e);
//        }


        try {
            return taskRepository.insertTimeEntry(timeEntry);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Failed to create time entry", e);
        }
    }

}
