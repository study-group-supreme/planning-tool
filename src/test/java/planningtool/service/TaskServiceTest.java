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

    @Test
    void createTimeEntry_throwsBadRequest_whenNullEntry(){
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(null)
        );
        assertThat(ex.getMessage()).contains("cannot be null");
    }

    @Test
    void createTimeEntry_throwsBadRequest_whenInvalidTaskId(){
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTimeSpent(new BigDecimal("1.0"));
        entry.setTaskId(0); // invalid

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("Invalid task id");
    }

    @Test
    void createTimeEntry_throwsBadRequest_whenTimeSpentNull(){
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(null); // invalid

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("positive number");
    }

    @Test
    void createTimeEntry_throwsBadRequest_whenTimeSpentZero(){
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(BigDecimal.ZERO);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("must be a positive number");
    }

    @Test
    void createTimeEntry_throwsBadRequest_whenTimeSpentNegative() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(new BigDecimal("-1.0"));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("positive number");
    }


}
