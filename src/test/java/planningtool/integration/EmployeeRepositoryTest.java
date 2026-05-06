package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void findEmployeeByEmail_shouldFindEmployeeWithCorrespondingEmail(){
        Employee employee = employeeRepository.findEmployeeByEmail("grey@email.com");
        assertThat(employee.getName()).isEqualTo("Andreas Jensen");
        assertThat(employee.isProjectManager()).isEqualTo(true);
        assertThat(employee.getId()).isEqualTo(1);
    }

    @Test
    void findEmployeeById_shouldFindEmployeeWithCorrespondingId(){
        Employee employee = employeeRepository.findEmployeeById(1);
        assertThat(employee.getName()).isEqualTo("Andreas Jensen");
        assertThat(employee.getEmail()).isEqualTo("grey@email.com");
        assertThat(employee.isProjectManager()).isEqualTo(true);
    }
}
