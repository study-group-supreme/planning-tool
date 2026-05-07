package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void login_ShouldThrowNotFoundExceptionIfEmailIsNotInDB(){
        when(employeeRepository.findEmployeeByEmail("turquoise@gmial.com")).thenThrow(new EmptyResultDataAccessException("Email not in the database",1));
        assertThrows(NotFoundException.class, () -> employeeService.login("turquoise@gmial.com", "notpw"));
    }

    @Test
    void login_ShouldThrowBadRequestExceptionIfPasswordIsIncorrect(){
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Andreas Jensen");
        employee.setEmail("grey@email.com");
        employee.setPassword("1234");
        employee.setProjectManager(true);
        when(employeeRepository.findEmployeeByEmail("grey@email.com")).thenReturn(employee);
        assertThrows(BadRequestException.class, () -> employeeService.login("grey@email.com", "5678"));
    }

    @Test
    void login_ShouldSucceedWithRightCredentials(){
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Andreas Jensen");
        employee.setEmail("grey@email.com");
        employee.setPassword("1234");
        employee.setProjectManager(true);
        when(employeeRepository.findEmployeeByEmail("grey@email.com")).thenReturn(employee);

        Employee result = employeeService.login("grey@email.com", "1234");
        assertEquals(1, result.getId());
        assertEquals("Andreas Jensen", result.getName());
    }

}
