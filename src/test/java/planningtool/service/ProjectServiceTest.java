package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;
import planningtool.exception.BadRequestException;
import planningtool.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_ThrowsBadRequestException_WhenTitleIsEmpty() {
        assertThrows(BadRequestException.class, () -> projectService.createProject(new Project()).getTitle().isEmpty());
    }

    @Test
    void createProject_ThrowsBadRequestException_WhenDescriptionIsOver1080Characters() {
        Project project = new Project();
        project.setTitle("test");
        project.setDescription("A".repeat(1081));
        assertThrows(BadRequestException.class, () -> projectService.createProject(project));
    }

    @Test
    void createProject_ThrowsBadRequestException_WhenTitleIsOver255Characters() {
        Project project = new Project();
        project.setTitle("M".repeat(256));
        assertThrows(BadRequestException.class, () -> projectService.createProject(project));
    }

    @Test
    void createProject_ThrowsBadRequestException_IfDeadlineIsBeforeToday() {
        Project project = new Project();
        project.setTitle("test");
        project.setDeadline(LocalDate.of(2020, 5, 6));
        assertThrows(BadRequestException.class, () -> projectService.createProject(project));
    }

    //TODO createProject() tests for DatabaseOperationException throw should be made

    @Test
    void createProject_timeOfCreation_shouldSetTimeOfCreationToNow() {
        Project project = new Project();
        project.setTitle("test");
        project.setDeadline(LocalDate.of(2028, 5, 3));

        when(projectRepository.insertProject(project)).thenReturn(project);
        Project result = projectService.createProject(project);
        assertThat(result.getTimeOfCreation()).isEqualTo(LocalDate.now());
    }

    @Test
    void createProject_isActive_shouldSetIsActiveToTrueOnCreating() {
        Project project = new Project();
        project.setTitle("test");
        project.setDeadline(LocalDate.of(2028, 5, 5));

        when(projectRepository.insertProject(project)).thenReturn(project);

        Project result = projectService.createProject(project);

        assertThat(result.isActive()).isTrue();

    }

    @Test
    void createProject_shouldCreateAProject() {
        Project project = new Project();
        project.setTitle("test");
        project.setDescription("test");
        project.setDeadline(LocalDate.of(2028, 2, 1));
        project.setProjectCreatorId(1);

        when(projectRepository.insertProject(project)).thenReturn(project);
        Project createdProject = projectService.createProject(project);

        assertThat(createdProject.getTitle()).isEqualTo("test");
        assertThat(createdProject.getDescription()).isEqualTo("test");
        assertThat(createdProject.getDeadline()).isEqualTo(LocalDate.of(2028, 2, 1));
        assertThat(createdProject.getProjectCreatorId()).isEqualTo(1);
        assertThat(createdProject.getTimeOfCreation()).isEqualTo(LocalDate.now());
        assertThat(createdProject.isActive()).isTrue();
    }

    @Test
    void getProjectMembersByProjectId_shouldShowAllProjectMembersConnectedToAProject() {
        Employee employee = new Employee();
        employee.setName("Hans");
        employee.setEmail("hans@email.com");
        employee.setId(1);
        employee.setPassword("123");
        employee.setRoleId(1);

        Employee employee1 = new Employee();
        employee1.setName("Fin");
        employee1.setId(2);
        employee1.setRoleId(2);
        employee1.setPassword("123");
        employee1.setEmail("fin@email.com");

        List<Employee> members = List.of(employee, employee1);

        when(projectRepository.findProjectMembersByProjectId(1)).thenReturn(members);

        List<Employee> result = projectService.getProjectMembersByProjectId(1);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Hans");
        assertThat(result.get(1).getName()).isEqualTo("Fin");
    }


    @Test
    void getProjectsByEmployeeId_ThrowsNotFoundException_WhenNoProjectsAreFoundConnectedToAnEmployeeId() {
        Employee employee = new Employee();
        employee.setId(1);
        when(projectRepository.findProjectsByEmployeeId(1)).thenReturn(List.of());
        when(employeeRepository.findEmployeeById(1)).thenReturn(employee);
        assertThrows(NotFoundException.class, () -> projectService.getProjectsByEmployeeId(1));
    }

    @Test
    void getProjectsByEmployeeId_shouldReturnListOfAllProjectsConnectedToAEmployeeId() {
        Employee employee = new Employee();
        employee.setId(1);

        Project project = new Project();
        project.setTitle("test");
        project.setId(1);
        project.setDeadline(LocalDate.of(2028, 2, 2));

        Project project1 = new Project();
        project1.setTitle("test2");
        project1.setId(2);
        project1.setDeadline(LocalDate.of(2029, 2, 2));

        List<Project> allProjects = List.of(project, project1);
        when(projectRepository.findProjectsByEmployeeId(1)).thenReturn(allProjects);
        List<Project> result = projectService.getProjectsByEmployeeId(1);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("test");
        assertThat(result.get(1).getTitle()).isEqualTo("test2");

    }

    @Test
    void addProjectMemberToProject_ShouldAddProjectMemberByCallingRepoInsertMethod() {
        Project project = new Project();
        project.setId(1);
        project.setProjectMembers(new ArrayList<>());

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Test");

        Employee addedEmployee = projectService.addProjectMemberToProject(employee, project);

        assertThat(addedEmployee.getName()).isEqualTo("Test");

        verify(projectRepository, times(1)).insertProjectMember(employee, project);
    }

    @Test
    void addProjectMemberToProject_ShouldThrowBadRequestException() {
        Project project = new Project();
        project.setId(1);

        Employee employee = new Employee();
        employee.setId(300);
        employee.setName("Test");

        project.setProjectMembers(new ArrayList<>(List.of(employee)));

        assertThrows(BadRequestException.class, () -> projectService.addProjectMemberToProject(employee, project));

        verify(projectRepository, never()).insertProjectMember(employee, project);
    }

    @Test
    void addProjectMemberToProject_ShouldThrowDatabaseOperationException() {
        Project project = new Project();
        project.setProjectMembers(new ArrayList<>());
        Employee employee = new Employee();

        doThrow(new DataIntegrityViolationException("")).when(projectRepository).insertProjectMember(employee, project);

        assertThrows(DatabaseOperationException.class, () -> {
            projectService.addProjectMemberToProject(employee, project);
        });
    }

    @Test
    void removeProjectMemberFromProject_ShouldRemoveMemberByCallingRepoDeleteMethod() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Test");

        Project project = new Project();
        project.setId(1);
        project.setProjectMembers(new ArrayList<>());
        project.setProjectMembers(List.of(employee));

        projectService.removeProjectMemberFromProject(employee, project);

        verify(projectRepository, times(1)).deleteProjectMember(employee, project);
    }

    @Test
    void removeProjectMemberFromProject_ShouldThrowNotFoundException() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Test");

        Project project = new Project();
        project.setId(1);
        project.setProjectMembers(new ArrayList<>());

        assertThrows(NotFoundException.class, () -> projectService.removeProjectMemberFromProject(employee, project));

        verify(projectRepository, never()).deleteProjectMember(employee, project);
    }

    @Test
    void removeProjectMemberToProject_ShouldThrowDatabaseOperationException() {
        Employee employee = new Employee();
        Project project = new Project();
        project.setProjectMembers(List.of(employee));

        doThrow(new DataIntegrityViolationException("")).when(projectRepository).deleteProjectMember(employee, project);

        assertThrows(DatabaseOperationException.class, () -> {
            projectService.removeProjectMemberFromProject(employee, project);
        });
    }

    @Test
    void archiveProject_ShouldCallRepository() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("test");
        project.setActive(true);
        projectService.archiveProject(1);
        verify(projectRepository).archiveProject(1);
    }

    @Test
    void archiveProject_ThrowsDatabaseOperationException_WhenRepositoryFails() {

        doThrow(new DataAccessException("DB error") {
        }).when(projectRepository).archiveProject(1);

        assertThrows(DatabaseOperationException.class,
                () -> projectService.archiveProject(1));

        verify(projectRepository).archiveProject(1);
    }


    @Test
    void restoreProject_ShouldCallRepository() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("test");
        project.setActive(false);
        projectService.restoreProject(1);
        verify(projectRepository).restoreProject(1);
    }

    @Test
    void restoreProject_ThrowsDatabaseOperationException_WhenRepositoryFails() {

        doThrow(new DataAccessException("DB error") {
        }).when(projectRepository).restoreProject(1);

        assertThrows(DatabaseOperationException.class,
                () -> projectService.restoreProject(1));

        verify(projectRepository).restoreProject(1);
    }

    @Test
    void getTotalEstimatedTimeForProject_ShouldSumAllMainTaskEntries(){
        Task t1 = new Task();
        t1.setId(10);
        t1.setTimeEstimate(new BigDecimal("2.5"));

        Task t2 = new Task();
        t2.setId(20);
        t2.setTimeEstimate(new BigDecimal("3.0"));

        List<Task> mainTasks = List.of(t1, t2);

        when(projectRepository.findMainTasksByProjectId(1))
                .thenReturn(mainTasks);

        // Mock repository calls inside getEstimatedTime()
        when(taskRepository.findTaskById(10)).thenReturn(t1);
        when(taskRepository.findSubtasksByParentId(10)).thenReturn(List.of());

        when(taskRepository.findTaskById(20)).thenReturn(t2);
        when(taskRepository.findSubtasksByParentId(20)).thenReturn(List.of());


        BigDecimal result = projectService.getTotalEstimatedTimeForProject(1);

        assertThat(result).isEqualTo(new BigDecimal("5.5"));
    }

    @Test
    void getTotalEstimatedTimeForProject_ShouldReturnZero_WhenNoMainTasks(){
        when(projectRepository.findMainTasksByProjectId(1))
                .thenReturn(List.of());

        BigDecimal result = projectService.getTotalEstimatedTimeForProject(1);

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getEstimatedTime_ForTask_SumsSubtaskEstimates_WhenParentHasSubtasks(){
        Task parent = new Task();
        parent.setId(1);
        parent.setParentTaskId(null);
        parent.setTimeEstimate(new BigDecimal("10.0")); // should be ignored

        Task s1 = new Task();
        s1.setTimeEstimate(new BigDecimal("2.0"));

        Task s2 = new Task();
        s2.setTimeEstimate(new BigDecimal("3.5"));

        when(taskRepository.findTaskById(1)).thenReturn(parent);
        when(taskRepository.findSubtasksByParentId(1)).thenReturn(List.of(s1, s2));

        BigDecimal result = projectService.getEstimatedTimeForTask(1);

        assertThat(result).isEqualByComparingTo("5.5");
    }

    @Test
    void getEstimatedTime_ForTask_ReturnsParentEstimate_WhenNoSubTasks(){
        Task parent = new Task();
        parent.setId(5);
        parent.setParentTaskId(null);
        parent.setTimeEstimate(new BigDecimal("8.0"));

        when(taskRepository.findTaskById(5)).thenReturn(parent);
        when(taskRepository.findSubtasksByParentId(5)).thenReturn(List.of());

        BigDecimal result = projectService.getEstimatedTimeForTask(5);

        assertThat(result).isEqualByComparingTo("8.0");
    }

    @Test
    void getEstimatedTime_ForTask_ReturnsOwnEstimate_WhenTaskIsSubtask(){
        Task subtask = new Task();
        subtask.setId(10);
        subtask.setParentTaskId(1);
        subtask.setTimeEstimate(new BigDecimal("3.5"));

        when(taskRepository.findTaskById(10)).thenReturn(subtask);

        BigDecimal result = projectService.getEstimatedTimeForTask(10);

        assertThat(result).isEqualByComparingTo("3.5");
        verify(taskRepository, never()).findSubtasksByParentId(anyInt());
    }


    @Test
    void editProject_ReturnsUpdatedProject() {
        Project originalProject = new Project();
        originalProject.setId(1);
        originalProject.setTitle("Original Title");
        originalProject.setDeadline(LocalDate.now().plusDays(2));
        originalProject.setDescription("Original Description");
        originalProject.setProjectCreatorId(1);
        originalProject.setTimeOfCreation(LocalDate.now());

        when(projectRepository.findProjectById(1)).thenReturn(originalProject);

        originalProject.setTitle("New title");
        originalProject.setDescription("New Description");
        originalProject.setDeadline(LocalDate.now().plusWeeks(1));

        Project result = projectService.editProject(originalProject);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getDeadline()).isEqualTo(LocalDate.now().plusWeeks(1));
        assertThat(result.getDescription()).isEqualTo("New Description");
        verify(projectRepository).updateProject(any());
    }

    @Test
    void editProject_ThrowsBadRequestException_WhenTitleIsEmpty(){
        Project project = new Project();
        project.setTitle("");
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowsBadRequestException_WhenTitleIsWhiteSpace(){
        Project project = new Project();
        project.setTitle("    ");
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowsBadRequestException_WhenTitleIsNull(){
        Project project = new Project();
        project.setTitle(null);
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowsBadRequestException_WhenTitleIsOver225Characters(){
        Project project = new Project();
        project.setTitle("T".repeat(226));
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowsBadRequestException_WhenDeadlineIsInThePast(){
        Project project = new Project();
        project.setDeadline(LocalDate.of(1999, 9, 5));
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowBadRequestException_WhenDescriptionIsTooLong(){
        Project project = new Project();
        project.setDescription("T".repeat(1081));
        assertThrows(BadRequestException.class, () -> projectService.editProject(project));
        verify(projectRepository, never()).updateProject(any());
    }

    @Test
    void editProject_ThrowDatabaseOperationException_IfDatabaseException(){
        Project project = new Project();
        project.setId(1);
        project.setTitle("Test");
        project.setDescription("Testing");
        project.setDeadline(LocalDate.now().plusWeeks(1));

        doThrow(new DataIntegrityViolationException("")).when(projectRepository).updateProject(project);

        DatabaseOperationException exception = assertThrows(DatabaseOperationException.class, () -> {projectService.editProject(project);
        });

        assertThat(exception.getMessage()).isEqualTo("Project could not be updated");

        verify(projectRepository, never()).findProjectById(anyInt());
    }

    @Test
    void getLoggedTimeForTask_ParentWithSubTasks_ShouldSumAllEntries(){
        int parentId = 30;

        Task subtask1 = new Task();
        subtask1.setId(31);

        Task subtask2 = new Task();
        subtask2.setId(32);

        TimeEntry parentEntry = new TimeEntry();
        parentEntry.setTimeSpent(new BigDecimal("1.0"));

        TimeEntry entry1 = new TimeEntry();
        entry1.setTimeSpent(new BigDecimal("2.0"));

        TimeEntry entry2 = new TimeEntry();
        entry2.setTimeSpent(new BigDecimal("3.0"));

        when(taskRepository.findSubtasksByParentId(parentId)).thenReturn(List.of(subtask1, subtask2));
        when(taskRepository.findTimeEntriesByTaskId(parentId)).thenReturn(List.of(parentEntry));
        when(taskRepository.findTimeEntriesByTaskId(31)).thenReturn(List.of(entry1));
        when(taskRepository.findTimeEntriesByTaskId(32)).thenReturn(List.of(entry2));

        BigDecimal result = projectService.getLoggedTimeForTask(parentId);

        assertThat(result).isEqualTo(new BigDecimal("6.0"));
    }

    @Test
    void getLoggedTimeForTask_ParentWithoutSubtasks_ShouldSumOwnEntries(){
        int taskId = 20;

        TimeEntry entry1 = new TimeEntry();
        entry1.setTimeSpent(new BigDecimal("1.0"));

        when(taskRepository.findSubtasksByParentId(taskId)).thenReturn(List.of());
        when(taskRepository.findTimeEntriesByTaskId(taskId)).thenReturn(List.of(entry1));

        BigDecimal result = projectService.getLoggedTimeForTask(taskId);

        assertThat(result).isEqualTo(new BigDecimal("1.0"));
    }

    @Test
    void getLoggedTimeForTask_Subtask_ShouldSumOwnEntries(){
        int taskId = 10;


        TimeEntry entry1 = new TimeEntry();
        entry1.setTimeSpent(new BigDecimal("1.5"));

        TimeEntry entry2 = new TimeEntry();
        entry2.setTimeSpent(new BigDecimal("2.0"));

        when(taskRepository.findTimeEntriesByTaskId(taskId)).thenReturn(List.of(entry1, entry2));

        BigDecimal result = projectService.getLoggedTimeForTask(taskId);

        assertThat(result).isEqualTo(new BigDecimal("3.5"));
    }

    @Test
    void getTotalLoggedTimeForProject_ShouldSumAllMainTaskEntries(){
        Task task1 = new Task();
        task1.setId(10);
        Task task2 = new Task();
        task2.setId(20);

        TimeEntry entry1 = new TimeEntry();
        entry1.setTimeSpent(new BigDecimal("2.5"));
        TimeEntry entry2 = new TimeEntry();
        entry2.setTimeSpent(new BigDecimal("3.0"));

        List<Task> mainTasks = List.of(task1, task2);
        when(projectRepository.findMainTasksByProjectId(1)).thenReturn(mainTasks);

        // Mock repository calls inside getLoggedTimeForTask()
        when(taskRepository.findSubtasksByParentId(10)).thenReturn(List.of());
        when(taskRepository.findTimeEntriesByTaskId(10)).thenReturn(List.of(entry1));

        when(taskRepository.findSubtasksByParentId(20)).thenReturn(List.of());
        when(taskRepository.findTimeEntriesByTaskId(20)).thenReturn(List.of(entry2));

        BigDecimal result = projectService.getTotalLoggedTimeForProject(1);
        assertThat(result).isEqualTo(new BigDecimal("5.5"));
    }

    @Test
    void getProjectTimeSummaries_ReturnsCorrectNestedMapForMultipleProjects() {
        // Two projects
        Project p1 = new Project();
        p1.setId(1);

        Project p2 = new Project();
        p2.setId(2);

        List<Project> projects = List.of(p1, p2);

        // Project 1 setup
        Task t1 = new Task();
        t1.setId(10);
        t1.setTimeEstimate(new BigDecimal("5.5"));

        when(projectRepository.findMainTasksByProjectId(1))
                .thenReturn(List.of(t1));

        TimeEntry e1 = new TimeEntry();
        e1.setTimeSpent(new BigDecimal("10.0"));

        when(taskRepository.findTaskById(10)).thenReturn(t1);
        when(taskRepository.findSubtasksByParentId(10)).thenReturn(List.of());
        when(taskRepository.findTimeEntriesByTaskId(10)).thenReturn(List.of(e1));

        // Project 2 setup
        Task t2 = new Task();
        t2.setId(20);
        t2.setTimeEstimate(new BigDecimal("3.0"));

        when(projectRepository.findMainTasksByProjectId(2))
                .thenReturn(List.of(t2));

        TimeEntry e2 = new TimeEntry();
        e2.setTimeSpent(new BigDecimal("7.5"));

        when(taskRepository.findTaskById(20)).thenReturn(t2);
        when(taskRepository.findSubtasksByParentId(20)).thenReturn(List.of());
        when(taskRepository.findTimeEntriesByTaskId(20)).thenReturn(List.of(e2));

        // Act
        Map<Integer, Map<String, BigDecimal>> result =
                projectService.getProjectTimeSummaries(projects);

        // Assert
        assertThat(result).containsKeys(1, 2);

        assertThat(result.get(1).get("estimate")).isEqualByComparingTo("5.5");
        assertThat(result.get(1).get("logged")).isEqualByComparingTo("10.0");

        assertThat(result.get(2).get("estimate")).isEqualByComparingTo("3.0");
        assertThat(result.get(2).get("logged")).isEqualByComparingTo("7.5");
    }

    @Test
    void calculateEstimatedPriceForTask_ShouldCalculatePriceOfTask_UsingTaskId(){
        Task task = new Task();
        task.setId(1);
        task.setAssignedMemberId(1);
        task.setTimeEstimate(new BigDecimal(8));

        Employee employee = new Employee();
        employee.setId(1);
        employee.setRoleId(1);

        when(employeeRepository.findPricePerHourByEmployeeId(1)).thenReturn(new BigDecimal(500));
        when(taskRepository.findTaskById(1)).thenReturn(task);

        BigDecimal result = projectService.calculateEstimatedPriceForTask(1);

        assertThat(result).isEqualTo(new BigDecimal(4000));
    }
    // TODO Make exception handling and test for exception scenario

    @Test
    void sumTimeEntriesForTask_ShouldReturnSumOfAllEntries(){
        int taskId = 10;

        TimeEntry entry1 = new TimeEntry();
        entry1.setTimeSpent(new BigDecimal("1.5"));

        TimeEntry entry2 = new TimeEntry();
        entry2.setTimeSpent(new BigDecimal("2.0"));

        TimeEntry entry3 = new TimeEntry();
        entry3.setTimeSpent(new BigDecimal("0.5"));

        when(taskRepository.findTimeEntriesByTaskId(taskId)).thenReturn(List.of(entry1, entry2, entry3));

        BigDecimal result = projectService.sumTimeEntriesForTask(taskId);

        assertThat(result).isEqualByComparingTo("4.0");
        verify(taskRepository).findTimeEntriesByTaskId(taskId);
    }

    @Test
    void sumTimeEntriesForTask_ShouldReturnZero_WhenNoEntries(){
        when(taskRepository.findTimeEntriesByTaskId(99)).thenReturn(List.of());

        BigDecimal result = projectService.sumTimeEntriesForTask(99);

        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void calculateCurrentCostOfProject_ShouldReturnCorrectPrice(){
        TimeEntry timeEntry1ForEmployee1 = new TimeEntry();
        timeEntry1ForEmployee1.setEmployeeId(1);
        timeEntry1ForEmployee1.setTimeSpent(new BigDecimal(2));

        TimeEntry timeEntry2ForEmployee1 = new TimeEntry();
        timeEntry2ForEmployee1.setEmployeeId(1);
        timeEntry2ForEmployee1.setTimeSpent(new BigDecimal(3));

        TimeEntry timeEntry1ForEmployee2 = new TimeEntry();
        timeEntry1ForEmployee2.setEmployeeId(2);
        timeEntry1ForEmployee2.setTimeSpent(new BigDecimal(4));

        TimeEntry timeEntry2ForEmployee2 = new TimeEntry();
        timeEntry2ForEmployee2.setEmployeeId(2);
        timeEntry2ForEmployee2.setTimeSpent(new BigDecimal(2));

        Task task1 = new Task();
        task1.setId(1);
        task1.setTimeEntries(List.of(timeEntry1ForEmployee1, timeEntry2ForEmployee2));

        Task task2 = new Task();
        task2.setId(2);
        task2.setTimeEntries(List.of(timeEntry2ForEmployee1, timeEntry1ForEmployee2));

        int projectId = 1;
        when(taskRepository.findTimeEntriesByTaskId(1)).thenReturn(List.of(timeEntry1ForEmployee1, timeEntry2ForEmployee2));
        when(taskRepository.findTimeEntriesByTaskId(2)).thenReturn(List.of(timeEntry2ForEmployee1, timeEntry1ForEmployee2));
        when(projectRepository.findTasksByProjectId(1)).thenReturn(List.of(task1, task2));
        when(employeeRepository.findPricePerHourByEmployeeId(1)).thenReturn(new BigDecimal("500.00"));
        when(employeeRepository.findPricePerHourByEmployeeId(2)).thenReturn(new BigDecimal("1000.00"));

        BigDecimal result = projectService.calculateCurrentCostOfProject(projectId);

        assertThat(result).isEqualTo(new BigDecimal("8500.00"));
    }

    @Test
    void calculateEstimatedPriceForProject(){
        Task task1 = new Task();
        task1.setTimeEstimate(new BigDecimal(3));
        task1.setAssignedMemberId(1);

        Task task2 = new Task();
        task2.setTimeEstimate(new BigDecimal(2));
        task2.setAssignedMemberId(2);

        int projectId = 1;

        when(projectRepository.findTasksByProjectId(1)).thenReturn(List.of(task1, task2));
        when(employeeRepository.findPricePerHourByEmployeeId(1)).thenReturn(new BigDecimal("500.00"));
        when(employeeRepository.findPricePerHourByEmployeeId(2)).thenReturn(new BigDecimal("1000.00"));

        BigDecimal result = projectService.calculateEstimatedPriceForProject(projectId);
        assertThat(result).isEqualTo(new BigDecimal("3500.00"));
    }

    // TODO add similar tests to EstimatedTime as logged-time tests

    // TODO getProjectById() tests should be made
}
