package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import planningtool.exception.BadRequestException;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import static org.mockito.Mockito.*;


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
// TODO This test is a bit wonky and we need to write more tests!
    @Test
    void createTask_returnsCreatedTask() {
        Task task = new Task();
        task.setId(3);
        task.setProjectId(2);
        task.setParentTaskId(null);
        task.setTitle("Sweep floors");
        task.setDescription("Lunch room");
        task.setHighPriority(false);
        task.setTimeEstimate(new BigDecimal("0.25"));

        when (taskRepository.insertTask(task)).thenReturn(task);
        Task createdTask = taskService.createTask(task);
        assertEquals(3,createdTask.getId());
        assertEquals(2, createdTask.getProjectId());
        assertNull(createdTask.getParentTaskId());
        assertEquals("Sweep floors", createdTask.getTitle());
        assertEquals("Lunch room", createdTask.getDescription());
        assertFalse(createdTask.isHighPriority());
        assertThat(createdTask.getTimeEstimate()).isEqualByComparingTo("0.25");
    }

    @Test
    void createTimeEntry_shouldCllRepositoryWhenValid() {
        // A TimeEntry populated with valid fields
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(new BigDecimal("1.5"));

        // insert returns same entry with id
        // A TimeEntry created to represent what the repository should return (same fields but with id = 10)
        TimeEntry saved = new TimeEntry();
        saved.setId(10);
        saved.setEmployeeId(1);
        saved.setTaskId(2);
        saved.setTimeSpent(new BigDecimal("1.5"));
        when(taskRepository.insertTimeEntry(entry)).thenReturn(saved);

        // The service method is called
        TimeEntry result = taskService.createTimeEntry(entry);

        // Verifies the service returned the repository result
        assertThat(result.getId()).isEqualTo(10);
        // verify the repository method was called with the same TimeEntry entry instance
        verify(taskRepository).insertTimeEntry(entry);
    }

}
