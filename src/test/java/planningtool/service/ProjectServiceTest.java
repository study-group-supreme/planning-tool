package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;
import planningtool.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeRepository employeeRepository;

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
        projectService.archiveProject(project);
        verify(projectRepository).archiveProject(project);
    }

    @Test
    void archiveProject_ThrowsDatabaseOperationException_WhenRepositoryFails() {
        Project testProject = new Project();

        doThrow(new DataAccessException("DB error") {
        })
                .when(projectRepository).archiveProject(testProject);

        assertThrows(DatabaseOperationException.class,
                () -> projectService.archiveProject(testProject));

        verify(projectRepository).archiveProject(testProject);
    }


    @Test
    void restoreProject_ShouldCallRepository() {
        Project project = new Project();
        project.setId(1);
        project.setTitle("test");
        project.setActive(false);
        projectService.restoreProject(project);
        verify(projectRepository).restoreProject(project);
    }

    @Test
    void restoreProject_ThrowsDatabaseOperationException_WhenRepositoryFails() {
        Project testProject = new Project();

        doThrow(new DataAccessException("DB error") {
        })
                .when(projectRepository).restoreProject(testProject);

        assertThrows(DatabaseOperationException.class,
                () -> projectService.restoreProject(testProject));

        verify(projectRepository).restoreProject(testProject);
    }


    // TODO getProjectById() tests should be made
}
