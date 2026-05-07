package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import planningtool.exception.BadRequestException;
import planningtool.model.Task;
import planningtool.repository.TaskRepository;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_throwsBadRequest_whenIdInvalid() {
        assertThrows(BadRequestException.class, () -> taskService.getTaskById(0));
    }

    @Test
    void createTask_returnsCreatedTask() {
        Task testTask = new Task();
        testTask.setId(3);
        testTask.setProjectId(2);
        testTask.setParentTaskId(null);
        testTask.setTitle("Sweep floors");
        testTask.setDescription("Lunch room");
        testTask.setHighPriority(false);
        testTask.setTimeEstimate(new BigDecimal("0.25"));


    }









}
