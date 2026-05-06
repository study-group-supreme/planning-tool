package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Employee;
import planningtool.repository.ProjectRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void fetchProjectMembersByProjectId_shouldReturnAllEmployeesInAProject() {
        List<Employee> employees = projectRepository.fetchProjectMembersByProjectId(1);
        assertThat(employees).hasSize(4);
        assertThat(employees.get(0).getName()).isEqualTo("Andreas Jensen");
        assertThat(employees.get(1).getName()).isEqualTo("August Skipper");
        assertThat(employees.get(2).getName()).isEqualTo("Daniella Norgren");
        assertThat(employees.get(3).getName()).isEqualTo("Mads Svanholm");

    }
}



