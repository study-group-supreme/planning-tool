package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import planningtool.exception.NotFoundException;
import planningtool.model.Project;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.util.List;

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

    @GetMapping()
    public String ShowListOfProjectsByEmployeeId(Model model, HttpSession session) {
        int employeeId = (Integer) session.getAttribute("employeeId");
        try {
            List<Project> employeeProjects = projectService.getProjectsByEmployeeId(employeeId);
            model.addAttribute("projects", employeeProjects);
            return "project/list-projects";
        } catch(NotFoundException e){
            model.addAttribute("emptyList", true);
            model.addAttribute("message", e.getMessage());
            return "project/list-projects";
        }
    }

    @GetMapping("/add")
    public String createProject(Model model) {
        model.addAttribute("project", new Project());
        return "project/create-project";
    }

    //Needs to have session included and needs a /projects Page
    @PostMapping("/add")
    public String createProject(@ModelAttribute Project project, HttpSession session) {
        Integer projectManagerId = (Integer) session.getAttribute("employeeId");
        project.setProjectCreatorId(projectManagerId);
        projectService.createProject(project);
        return "redirect:/projects";
    }

    @GetMapping("/{projectId}")
    public String showSpecificProject(@PathVariable int projectId, Model model) {
        model.addAttribute("project", projectService.getProjectById(projectId));
        model.addAttribute("mainTask", false);
        return "project/details-project";
    }

}
