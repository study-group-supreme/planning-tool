package planningtool.service;

import org.springframework.dao.EmptyResultDataAccessException;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;

public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public Employee login(String email, String password){
        try{
            Employee employeeLoginIn = employeeRepository.findEmployeeByEmail(email);
            if(!employeeLoginIn.getPassword().equals(password)){
                throw new BadRequestException("Incorrect password");
            }
            return employeeLoginIn;
        } catch (EmptyResultDataAccessException e){
            throw new NotFoundException("User not found");
        }
    }
}
