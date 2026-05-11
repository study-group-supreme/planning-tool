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

    // TODO We need to validation test these in the TaskServiceTest class!
    @Transactional
    public Task createTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (task.getTitle().length() > 225) {
            throw new BadRequestException("Task title cannot exceed 225 characters");
        }
        if (task.getDescription().length() > 1080) {
            throw new BadRequestException("Task description cannot exceed 1080 characters");
        }
        if (task.getTimeEstimate() != null && task.getTimeEstimate().compareTo(new BigDecimal("9999.99")) > 0) {
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
        if (timeEntry == null) {
            // For future us: DIFFERENT EXCEPTION HERE!!!!!!!!
            throw new RuntimeException("Error: Time Entry was null");
        }
        if (timeEntry.getTaskId() <= 0) {
            throw new BadRequestException("Invalid task id");
        }


        BigDecimal timeSpent = timeEntry.getTimeSpent();
        if (timeSpent == null || timeSpent.compareTo(BigDecimal.ZERO) <=0) {
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

}
