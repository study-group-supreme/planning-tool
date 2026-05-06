package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.repository.ProjectRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void findProjectMembersByProjectId_shouldReturnAllEmployeesInAProject() {
        List<Employee> employees = projectRepository.findProjectMembersByProjectId(1);
        assertThat(employees).hasSize(4);
        assertThat(employees.get(0).getName()).isEqualTo("Andreas Jensen");
        assertThat(employees.get(1).getName()).isEqualTo("August Skipper");
        assertThat(employees.get(2).getName()).isEqualTo("Daniella Norgren");
        assertThat(employees.get(3).getName()).isEqualTo("Mads Svanholm");
    }
    @Test
    void findTasksByProjectId_shouldReturnTasksWithTheSameProjectId(){
        List<Task> tasks = projectRepository.findTasksByProjectId(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Brew coffee");
        assertThat(tasks).hasSize(3);
    }
    @Test
    void insertProject_ShouldCreateNewProject(){
        Project newProject = new Project();
        newProject.setTitle("test");
        newProject.setDescription("tester");
        newProject.setTimeOfCreation(LocalDate.of(2026, 5, 6));
        newProject.setDeadline(LocalDate.of(2027, 5, 6));
        newProject.setProjectManagerId(1);
        newProject.setActive(true);

        Project result = projectRepository.insertProject(newProject);

        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getDescription()).isEqualTo("tester");
        assertThat(result.getProjectManagerId()).isEqualTo(1);
        assertThat(result.isActive()).isEqualTo(true);
        assertThat(result.getDeadline()).isEqualTo(LocalDate.of(2027, 5, 6));
        assertThat(result.getTimeOfCreation()).isEqualTo(LocalDate.of(2026, 5, 6));
    }
}



