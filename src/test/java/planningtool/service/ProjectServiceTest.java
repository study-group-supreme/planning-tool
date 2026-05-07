package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;
import planningtool.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

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
        project.setProjectManagerId(1);

        when(projectRepository.insertProject(project)).thenReturn(project);
        Project createdProject = projectService.createProject(project);

        assertThat(createdProject.getTitle()).isEqualTo("test");
        assertThat(createdProject.getDescription()).isEqualTo("test");
        assertThat(createdProject.getDeadline()).isEqualTo(LocalDate.of(2028, 2, 1));
        assertThat(createdProject.getProjectManagerId()).isEqualTo(1);
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
        employee.setProjectManager(true);

        Employee employee1 = new Employee();
        employee1.setName("Fin");
        employee1.setId(2);
        employee1.setProjectManager(false);
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
    void getProjectMembersByProjectId_ThrowsNotFoundException_WhenNoMembersAreAssignedToAProject() {
        when(projectRepository.findProjectMembersByProjectId(1)).thenReturn(List.of());
        assertThrows(NotFoundException.class, () -> projectService.getProjectMembersByProjectId(1));
    }
}
