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
        if (task.getTitle().length() > 255) {
            throw new BadRequestException("Task title cannot exceed 255 characters");
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            throw new BadRequestException("Description cannot be empty");
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


}
