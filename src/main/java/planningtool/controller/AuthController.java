package planningtool.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import planningtool.service.EmployeeService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String showLoginForm(){return "auth/login";}
}
