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
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;


import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

    //TODO Go through and verify if repository methods have been called in cases where the exceptions are thrown by the database

    @Test
    void getTaskById_ReturnsTask() {
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
        verify(taskRepository, never()).findTaskById(0);
    }

    @Test
    void getTaskById_ThrowsNotFoundException_WhenTaskDoesNotExist() {
        when(taskRepository.findTaskById(99))
                .thenThrow(new EmptyResultDataAccessException(1));

        NotFoundException ex = assertThrows(NotFoundException.class, () -> taskService.getTaskById(99));

        assertThat(ex.getMessage().contains("Database error"));
        verify(taskRepository).findTaskById(99);
    }

    @Test
    void getTaskById_ThrowsDatabaseOperationException_OnDataAccessError(){
        when(taskRepository.findTaskById(1))
                .thenThrow(new DataAccessException("DB down") {});


        DatabaseOperationException ex = assertThrows(DatabaseOperationException.class, () -> taskService.getTaskById(1));
        assertThat(ex.getMessage()).contains("Database error");
        verify(taskRepository).findTaskById(1);
    }

    @Test
    void createTask_returnsCreatedTask() {
        Task task = new Task();
        task.setId(3);
        task.setProjectId(2);
        task.setParentTaskId(null);
        task.setAssignedMemberId(1);
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
        verify(taskRepository).insertTask(any());
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
    void createTask_ThrowsBadRequestException_IfAssignedMemberIsNull(){
        Task task = new Task();
        task.setAssignedMemberId(null);
        assertThrows(BadRequestException.class, () -> taskService.createTask(task));
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsBadRequestException_IfAssignedMemberIsNotOnProject(){
        Task task = new Task();
        task.setProjectId(1);
        task.setAssignedMemberId(2);

        when(projectService.getProjectMemberIdsByProjectId(1)).thenReturn(List.of(1));

        assertThrows(BadRequestException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ThrowsDatabaseOperationException_WhenRepositoryFails(){
        Task task = new Task();
        task.setTitle("Title");
        task.setAssignedMemberId(1);

        when(taskRepository.insertTask(task))
                .thenThrow(new DataIntegrityViolationException("constraint"));

        DatabaseOperationException ex = assertThrows(
                DatabaseOperationException.class,
                () -> taskService.createTask(task)
        );

        assertThat(ex.getMessage()).contains("creation failed");
        verify(taskRepository).insertTask(any());
    }

    @Test
    void createTask_AllowsSubtask_WhenParentIsMainTask(){
        // A success case for creating a subtask
        // Subtasks are not allowed when their parent is already a subtask
        Task parent = new Task();
        parent.setId(10);
        parent.setAssignedMemberId(1);
        parent.setParentTaskId(null); // Makes it a main task

        Task subtask = new Task();
        subtask.setAssignedMemberId(1);
        subtask.setTitle("Subtask");
        subtask.setParentTaskId(10);

        when(taskRepository.findTaskById(10)).thenReturn(parent);
        when(taskRepository.insertTask(subtask)).thenReturn(subtask);

        Task result = taskService.createTask(subtask);

        assertThat(result).isNotNull();
        verify(taskRepository).findTaskById(10);
        verify(taskRepository).insertTask(subtask);
    }

    @Test
    void createTask_ThrowsBadRequest_WhenParentIsAlreadySubtask() {
        Task parentSubtask = new Task();
        parentSubtask.setId(20);
        parentSubtask.setParentTaskId(5); // is already a subtask

        Task newSubtask = new Task();
        newSubtask.setTitle("Invalid");
        newSubtask.setParentTaskId(20);

        when(taskRepository.findTaskById(20)).thenReturn(parentSubtask);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> taskService.createTask(newSubtask));

        assertThat(ex.getMessage()).contains("cannot have their own subtasks");
        verify(taskRepository).findTaskById(20);
        verify(taskRepository, never()).insertTask(any());
    }

    @Test
    void createTask_ClearsParentTimeEstimate_WhenCreatingFirstSubtask(){
        Task parent = new Task();
        parent.setId(1);
        parent.setProjectId(1);
        parent.setParentTaskId(null);
        parent.setAssignedMemberId(1);
        parent.setTimeEstimate(new BigDecimal("5.0"));

        Task subtask = new Task();
        subtask.setProjectId(1);
        subtask.setParentTaskId(1);
        subtask.setAssignedMemberId(1);
        subtask.setTitle("Subtask");

        when(taskRepository.findTaskById(1)).thenReturn(parent);
        when(taskRepository.insertTask(subtask)).thenReturn(subtask);

        taskService.createTask(subtask);

        verify(taskRepository).updateTask(argThat(t ->
                t.getId() == 1 && t.getTimeEstimate() == null
        ));
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
        verify(taskRepository).updateTask(any());
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

    //TODO editTask() test for DatabaseOperationException throw should be made


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
    void createTimeEntry_ThrowsBadRequest_whenTimeSpentTooLarge() {
        TimeEntry entry = new TimeEntry();
        entry.setTaskId(1);
        entry.setEmployeeId(1);
        entry.setTimeSpent(new BigDecimal("10000.00"));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> taskService.createTimeEntry(entry));
        assertThat(ex.getMessage().contains("cannot exceed 9999.99"));
        verify(taskRepository, never()).insertTimeEntry(entry);
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
        verify(taskRepository).insertTimeEntry(any());
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
    void removeTaskById_ShouldSucceed_WhenAllSubTasksAreDone() {
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
    void removeTaskById_ThrowsBadRequestException_IfYouTryToDeleteMainTaskBeforeSubtasks() {
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

    @Test
    void removeTimeEntryById_ShouldCallRepository(){
        TimeEntry entry = new TimeEntry();
        entry.setId(10);
        entry.setEmployeeId(1);
        entry.setTaskId(5);
        entry.setTimeSpent(new BigDecimal("1.0"));
        taskService.removeTimeEntryById(10);
        verify(taskRepository).deleteTimeEntryById(10);
    }

    @Test
    void removeTimeEntryById_ThrowsDatabaseOperationException_WhenRepositoryFails(){
        TimeEntry entry = new TimeEntry();
        entry.setId(1);
        entry.setTaskId(5);
        entry.setTimeSpent(new BigDecimal("1.5"));

        doThrow(new DataAccessException("DB error") {})
                .when(taskRepository).deleteTimeEntryById(1);

        assertThrows(DatabaseOperationException.class,
                () -> taskService.removeTimeEntryById(1));

        verify(taskRepository).deleteTimeEntryById(1);
    }

    @Test
    void getTimeEntriesByTaskId_ReturnsListOfTimeEntries() {
        TimeEntry entry1 = new TimeEntry();
        entry1.setId(1);
        entry1.setTaskId(5);
        entry1.setTimeSpent(new BigDecimal("1.5"));

        TimeEntry entry2 = new TimeEntry();
        entry2.setId(2);
        entry2.setTaskId(5);
        entry2.setTimeSpent(new BigDecimal("2.5"));

        when(taskRepository.findTimeEntriesByTaskId(5))
                .thenReturn(List.of(entry1, entry2));

        List<TimeEntry> result = taskService.getTimeEntriesByTaskId(5);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTimeSpent()).isEqualByComparingTo("1.5");
        verify(taskRepository).findTimeEntriesByTaskId(5);
    }

    @Test
    void hasChildren_ReturnsTrue_WhenRepositoryReturnsTrue(){
        when(taskRepository.taskHasChildren(5)).thenReturn(true);

        boolean result = taskService.hasChildren(5);
        assertTrue(result);
        verify(taskRepository).taskHasChildren(5);
    }

    @Test
    void hasChildren_ReturnsFalse_WhenRepositoryReturnsFalse(){
        when(taskRepository.taskHasChildren(5)).thenReturn(false);

        boolean result = taskService.hasChildren(5);
        assertFalse(result);
        verify(taskRepository).taskHasChildren(5);
    }



    //TODO editTaskIsDoneStatus() tests should be made (check validation first)

    @Test
    void getDoneTasks_ShouldReturnAListOfDoneTaskInAProject() {
        Task task = new Task();
        task.setParentTaskId(1);
        task.setId(1);
        task.setTitle("test");
        task.setDone(true);

        Task task1 = new Task();
        task1.setParentTaskId(1);
        task1.setId(2);
        task1.setTitle("test2");
        task1.setDone(true);

        Task task2 = new Task();
        task2.setParentTaskId(1);
        task2.setId(3);
        task2.setDone(false);

        List<Task> subtasks = List.of(task1, task, task2);

        int result = taskService.getDoneSubtasks(subtasks);

        assertThat(result).isEqualTo(2);
    }
    @Test
    void getAllSubtasks_ShouldReturnAListOfAllSubtasks(){
        Task task = new Task();
        task.setDone(true);
        task.setParentTaskId(1);
        task.setId(1);

        Task task1 = new Task();
        task1.setDone(false);
        task1.setId(2);
        task1.setParentTaskId(1);

        List<Task> subtasks = List.of(task, task1);
        int result = taskService.getTotalSubtasks(subtasks);
        assertThat(result).isEqualTo(2);
    }
    @Test
    void getTasksByParentId_shouldReturnListOfTasksWithSameParentId(){
        Task task = new Task();
        task.setId(1);

        Task task1 = new Task();
        task1.setId(2);
        task1.setParentTaskId(1);

        Task task2 = new Task();
        task2.setId(3);
        task2.setParentTaskId(1);

        List<Task> allTasks = List.of(task,task1,task2);
       when(taskRepository.findSubtasksByParentId(task1.getParentTaskId())).thenReturn(allTasks);
taskService.getTasksByParentId(1);

assertThat(allTasks.get(1).getId()).isEqualTo(2);
assertThat(allTasks.get(2).getId()).isEqualTo(3);
    }
    @Test
    void removeTaskById_ShouldThrowBadRequestException_WhenDeletingMainTaskBeforeAllSubtasks(){
        Task task = new Task();
        task.setId(1);
        task.setTitle("Main task");

        Task task1 = new Task();
        task1.setId(2);
        task1.setParentTaskId(1);
        task1.setTitle("Subtask");
        task1.setDone(false);

        List<Task> subtasks = List.of(task1);
        when(taskRepository.findSubtasksByParentId(1)).thenReturn(subtasks);
        when(taskRepository.findTaskById(1)).thenReturn(task);

       assertThrows(BadRequestException.class, () -> taskService.removeTaskById(1));
        verify(taskRepository, never()).deleteTaskById(1);
    }
}
