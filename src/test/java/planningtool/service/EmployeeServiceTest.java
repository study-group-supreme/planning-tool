package planningtool.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
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
    void login_ShouldThrowBadRequestIfEmailIsNotInDB(){
        when(employeeRepository.findEmployeeByEmail("turquoise@gmial.com")).thenThrow(new EmptyResultDataAccessException("Email not in the database",1));
        assertThrows(NotFoundException.class, () -> employeeService.login("turquoise@gmial.com", "notpw"));
    }
}
