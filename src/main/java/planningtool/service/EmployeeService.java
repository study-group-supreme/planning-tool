package planningtool.service;

import org.springframework.stereotype.Service;
import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;
@Service

public class EmployeeService {
    private EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

//    public Employee login(String email, String password){
//        try{
//            Employee employeeLoginIn = employeeRepository.findEmployeeByEmail(email);
//        }
//    }
}
