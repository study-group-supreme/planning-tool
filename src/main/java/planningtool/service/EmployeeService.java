package planningtool.service;

import planningtool.model.Employee;
import planningtool.repository.EmployeeRepository;

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
