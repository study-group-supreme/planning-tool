package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            model.addAttribute("employeeName", employeeService.getEmployeeById(employeeId).getName());
            model.addAttribute("date", LocalDate.now());
            return "project/list-projects";
        } catch (NotFoundException e) {
            model.addAttribute("emptyList", true);
            model.addAttribute("message", e.getMessage());
            return "project/list-projects";
        }
    }

    @GetMapping("/add")
    public String createProject(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("employee", employeeService.getAllEmployees());
        return "project/create-project";
    }

    //Needs to have session included and needs a /projects Page
    @PostMapping("/add")
    public String createProject(@ModelAttribute Project project, HttpSession session) {
        Integer projectManagerId = (Integer) session.getAttribute("employeeId");
        project.setProjectCreatorId(projectManagerId);
        projectService.createProject(project);
        return "redirect:/projects/add-member/" + project.getId();
    }

    @GetMapping("/{projectId}")
    public String showSpecificProject(@PathVariable int projectId, Model model) {
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);
        model.addAttribute("mainTask", false);
        model.addAttribute("progressMap", taskService.getMainTaskProgress(projectId));
        return "project/details-project";
    }

    @GetMapping("/add-member/{projectId}")
    public String addProjectMember(@PathVariable int projectId, Model model) {
        List<Employee> employeesNotOnProject = projectService.getEmployeesNotOnProject(projectId);
        List<Employee> employeesOnProject = projectService.getProjectMembersByProjectId(projectId);
        model.addAttribute("projectMembers", employeesOnProject);
        model.addAttribute("projectId", projectId);
        model.addAttribute("employees", employeesNotOnProject);
        return "project/add-member";
    }

    @PostMapping("/{projectId}/add-member")
    public String addProjectMember(@RequestParam int employeeId, @PathVariable int projectId) {
       Employee employee = employeeService.getEmployeeById(employeeId);
       Project project = projectService.getProjectById(projectId);
        projectService.addProjectMemberToProject(employee, project);
        return "redirect:/projects/add-member/" + projectId;
    }
}
