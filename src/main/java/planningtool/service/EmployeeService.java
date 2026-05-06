package planningtool.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;
@Service

public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee login(String email, String password){
        Employee employee;
        try{
            employee = employeeRepository.findEmployeeByEmail(email);
        } catch(EmptyResultDataAccessException e){
            throw new NotFoundException("Email is not in the system");
        }
        if (!employee.getPassword().equals(password)){
            throw new BadRequestException("Incorrect password");
        }
        return employee;
    }
}
