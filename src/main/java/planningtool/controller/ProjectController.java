package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import planningtool.model.Project;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

@RequestMapping("/projects")
@Controller
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;
    private final EmployeeService employeeService;

    public ProjectController(ProjectService projectService, TaskService taskService, EmployeeService employeeService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.employeeService = employeeService;
    }

    @GetMapping("/new")
    public String createProject(Model model) {
        model.addAttribute("project", new Project());
        return "project/create-project";
    }
    //Needs to have session included and needs a /projects Page
    @PostMapping("/save")
    public String saveProject(@ModelAttribute Project project, HttpSession session) {
        projectService.createProject(project);
        return "redirect:";
    }
}
