package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void getTaskById_ReturnsTask(){
        Task task = new Task();
        task.setId(1);
        task.setTitle("Brew coffee");

        when(taskRepository.findTaskById(1)).thenReturn(task);

        Task result = taskService.getTaskById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Brew coffee");
        verify(taskRepository).findTaskById(1);
    }

    @Test
    void getTaskById_throwsBadRequest_whenIdInvalid() {

        BadRequestException ex = assertThrows(BadRequestException.class, () -> taskService.getTaskById(0));
        assertThat(ex.getMessage().contains("Invalid task"));
    }

    @Test
    void getTaskById_ThrowsNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findTaskById(99))
                .thenThrow(new EmptyResultDataAccessException(1));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> taskService.getTaskById(99));

        assertThat(ex.getMessage().contains("Database error"));

    }

    @Test
    void getTaskById_ThrowsDatabaseOperationException_OnDataAccessError(){
        when(taskRepository.findTaskById(1))
                .thenThrow(new DataAccessException("DB down") {});


        DatabaseOperationException ex = assertThrows(DatabaseOperationException.class, () -> taskService.getTaskById(1));
        assertThat(ex.getMessage()).contains("Database error");

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

        when(taskRepository.insertTask(task)).thenReturn(task);
        Task createdTask = taskService.createTask(task);
        assertEquals(3, createdTask.getId());
        assertEquals(2, createdTask.getProjectId());
        assertNull(createdTask.getParentTaskId());
        assertEquals("Sweep floors", createdTask.getTitle());
        assertEquals("Lunch room", createdTask.getDescription());
        assertFalse(createdTask.isHighPriority());
        assertThat(createdTask.getTimeEstimate()).isEqualByComparingTo("0.25");
    }

    @Test
    void createTask_ThrowsBadRequestException_WhenTitleIsEmpty() {
        Task task = new Task();
        task.setTitle("");
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }
    @Test
    void createTask_ThrowsBadRequestException_WhenTitleIsNull() {
        Task task = new Task();
        task.setTitle(null);
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsBadRequestException_WhenDescriptionIsOver1080Characters() {
        Task task = new Task();
        task.setTitle("testTask");
        task.setDescription("B".repeat(1081));
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsBadRequestException_WhenTitleIsOver225Characters() {
        Task task = new Task();
        task.setTitle("A".repeat(226));
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsBadRequestException_WhenTimeEstimateExceedsLimit() {
        Task task = new Task();
        task.setTimeEstimate(new BigDecimal("10000.99"));
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsDatabaseOperationException_WhenRepositoryFails(){
        Task task = new Task();
        task.setTitle("Title");

        when(taskRepository.insertTask(task))
                .thenThrow(new DataIntegrityViolationException("constraint"));

        DatabaseOperationException ex = assertThrows(
                DatabaseOperationException.class,
                () -> taskService.createTask(task)
        );

        assertThat(ex.getMessage()).contains("creation failed");
    }

    @Test
    void editTask_ReturnsUpdatedTask() {
        Task originalTask = new Task();
        originalTask.setId(1);
        originalTask.setProjectId(1);
        originalTask.setParentTaskId(1);
        originalTask.setTitle("test");
        originalTask.setDescription("just a test");
        originalTask.setHighPriority(false);
        originalTask.setTimeEstimate(new BigDecimal("0.5"));

        when(taskRepository.findTaskById(1)).thenReturn(originalTask);

        originalTask.setParentTaskId(2);
        originalTask.setTitle("updated test");
        originalTask.setDescription("just a test, again");
        originalTask.setHighPriority(true);
        originalTask.setTimeEstimate(new BigDecimal("1"));

        Task result = taskService.editTask(originalTask);

        assertThat(result.getParentTaskId()).isEqualTo(2);
        assertThat(result.getTitle()).isEqualTo("updated test");
        assertThat(result.getDescription()).isEqualTo("just a test, again");
        assertTrue(result.isHighPriority());
        assertThat(result.getTimeEstimate()).isEqualByComparingTo(new BigDecimal("1"));
    }

    @Test
    void editTask_ThrowsBadRequestException_WhenTitleIsEmpty() {
        Task task = new Task();
        task.setTitle("");
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }
    @Test
    void editTask_ThrowsBadRequestException_WhenTitleIsWhiteSpace() {
        Task task = new Task();
        task.setTitle("  ");
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }
    @Test
    void editTask_ThrowsBadRequestException_WhenTitleIsNull() {
        Task task = new Task();
        task.setTitle(null);
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }
    @Test
    void editTask_ThrowsBadRequestException_WhenTitleIsOver225Characters() {
        Task task = new Task();
        task.setTitle("T".repeat(226));
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }
    @Test
    void editTask_ThrowsBadRequestException_WhenDescriptionIsOver1080Characters() {
        Task task = new Task();
        task.setTitle("test");
        task.setDescription("T".repeat(1081));
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }
    @Test
    void editTask_ThrowsBadRequestException_WhenTimeEstimateExceedsLimit() {
        Task task = new Task();
        task.setTitle("test");
        task.setDescription("test description");
        task.setTimeEstimate(new BigDecimal("10000.99"));
        assertThrows(BadRequestException.class, () -> taskService.editTask(task));
        verify(taskRepository, never()).updateTask(any());
    }


    @Test
    void createTimeEntry_ShouldCallRepositoryWhenValid() {
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
    void createTimeEntry_ThrowsRuntime_whenNullEntry() {
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> taskService.createTimeEntry(null)
        );
        assertThat(ex.getMessage()).contains("null");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsBadRequest_whenInvalidTaskId() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTimeSpent(new BigDecimal("1.0"));
        entry.setTaskId(0); // invalid

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("Invalid task id");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsBadRequest_whenTimeSpentNull() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(null); // invalid

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("positive number");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsBadRequest_whenTimeSpentZero() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(BigDecimal.ZERO);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("must be a positive number");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsBadRequest_whenTimeSpentNegative() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(new BigDecimal("-1.0"));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("positive number");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsDatabaseOperation_WhenRepositoryFails() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(new BigDecimal("1.0"));

        when(taskRepository.insertTimeEntry(entry))
                .thenThrow(new DataIntegrityViolationException("DB error"));

        DatabaseOperationException ex = assertThrows(
                DatabaseOperationException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("Failed to create time entry");
    }

    @Test
    void createTimeEntry_ThrowsNotFound_WhenTaskIdNotFoundInRepository() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(99);
        entry.setTimeSpent(new BigDecimal("1.0"));

        // Simulate repository not finding the task
        when(taskRepository.findTaskById(99))
                .thenThrow(new EmptyResultDataAccessException(1));

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("Nothing to show");
        verify(taskRepository, never()).insertTimeEntry(any());
    }

    @Test
    void createTimeEntry_ThrowsDatabaseOperation_WhenRepositoryFailsForFetchingTaskId() {
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(99);
        entry.setTimeSpent(new BigDecimal("1.0"));

        // Simulate database failure unrelated to "not found"
        when(taskRepository.findTaskById(99))
                .thenThrow(new DataAccessException("DB failure") {
                });

        DatabaseOperationException ex = assertThrows(
                DatabaseOperationException.class,
                () -> taskService.createTimeEntry(entry)
        );

        assertThat(ex.getMessage()).contains("Database error");
        verify(taskRepository, never()).insertTimeEntry(any());

    }

    @Test
    void removeTaskById_ShouldRemoveTaskById() {
        Task task = new Task();
        task.setId(1);
        task.setParentTaskId(1);
        task.setTitle("test");
        when(taskRepository.findTaskById(1)).thenReturn(task);
        taskService.removeTaskById(1);
        verify(taskRepository).deleteTaskById(1);
    }

    @Test
    void removeTaskById_ShouldSucceed_WhenAllSubTasksAreDone(){
        Task mainTask = new Task();
        mainTask.setId(1);
        mainTask.setTitle("Main Task");

        Task subTask = new Task();
        subTask.setId(2);
        subTask.setParentTaskId(1);
        subTask.setDone(true);

        when(taskRepository.findTaskById(1)).thenReturn(mainTask);
        when(taskRepository.findSubtasksByParentId(1)).thenReturn(List.of(subTask));

        taskService.removeTaskById(1);
        verify(taskRepository).deleteTaskById(1);
    }

    @Test
    void removeTaskById_ThrowsBadRequestException_IfYouTryToDeleteMainTaskBeforeSubtasks(){
        Task task = new Task();
        task.setId(1);
        task.setTitle("test");

        Task subtask = new Task();
        subtask.setId(2);
        subtask.setTitle("test2");
        subtask.setParentTaskId(1);

        Task subtask2 = new Task();
        subtask2.setId(3);
        subtask2.setTitle("test3");
        subtask2.setParentTaskId(1);

        List<Task> subtasks = List.of(subtask, subtask2);

        when(taskRepository.findTaskById(1)).thenReturn(task);
        when(taskRepository.findSubtasksByParentId(1)).thenReturn(subtasks);

        assertThrows(BadRequestException.class, () -> taskService.removeTaskById(1));

    }



}
