package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;

import java.math.BigDecimal;
import java.util.List;

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
        assertThat(employee.getId()).isEqualTo(1);
    }

    @Test
    void findEmployeeById_shouldFindEmployeeWithCorrespondingId(){
        Employee employee = employeeRepository.findEmployeeById(1);
        assertThat(employee.getName()).isEqualTo("Andreas Jensen");
        assertThat(employee.getEmail()).isEqualTo("grey@email.com");
    }

    @Test
    void findAllEmployees_shouldFindAllEmployees(){
        List<Employee> employees = employeeRepository.findAllEmployees();
        assertThat(employees.size()).isEqualTo(4);
        assertThat(employees.get(0).getName()).isEqualTo("Andreas Jensen");
        assertThat(employees.get(1).getName()).isEqualTo("August Skipper");
        assertThat(employees.get(2).getName()).isEqualTo("Daniella Norgren");
        assertThat(employees.get(3).getName()).isEqualTo("Mads Svanholm");
    }

    @Test
    void findEmployeesNotOnProject_ShouldReturnEmployeesNotAssignedToSpecificProject(){
        List<Employee> result = employeeRepository.findEmployeesNotOnProject(2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Andreas Jensen");
        assertThat(result.get(1).getName()).isEqualTo("August Skipper");
    }

    @Test
    void findPricePerHourByEmployeeId_ShouldReturnPricePerHourForSpecificEmployee(){
        BigDecimal pricePerHour = employeeRepository.findPricePerHourByEmployeeId(1);

        assertThat(pricePerHour).isEqualTo(new BigDecimal("1000.00"));
    }
}
