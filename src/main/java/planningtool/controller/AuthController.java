package planningtool.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.service.EmployeeService;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final EmployeeService employeeService;

    public AuthController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    // change from wishlist is adding model and "error" attribute for html
    @GetMapping("/login")
    public String showLoginForm(Model model){
        model.addAttribute("error", false);
        return "auth/login";}

    @PostMapping("/login")
    public String loginFormHandler(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model){


        try{
            Employee employee = employeeService.login(email, password);
            session.setAttribute("employeeId", employee.getId());
            session.setAttribute("employeeName", employee.getName());
            return "redirect:/projects";
        } catch (NotFoundException | BadRequestException e) {
            model.addAttribute("error", true);
            model.addAttribute("message", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/auth/login";
    }

}
