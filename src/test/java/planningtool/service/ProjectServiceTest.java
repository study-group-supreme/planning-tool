package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import planningtool.model.Project;
import planningtool.repository.ProjectRepository;
import planningtool.exception.BadRequestException;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

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
    void createProject_shouldCreateAProject(){
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
}
