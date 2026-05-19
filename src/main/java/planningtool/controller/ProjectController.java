package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.math.BigDecimal;
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
    public String createProject(@ModelAttribute Project project, HttpSession session, RedirectAttributes attributes) {
        try {
            Integer projectManagerId = (Integer) session.getAttribute("employeeId");
            project.setProjectCreatorId(projectManagerId);
            projectService.createProject(project);
            return "redirect:/projects/add-member/" + project.getId();
        }catch (BadRequestException e){
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/projects/add";
        }
    }

    @GetMapping("/{projectId}")
    public String showSpecificProject(@PathVariable int projectId, Model model, HttpSession session) {
        Project project = projectService.getProjectById(projectId);
        Integer currentUserId = (Integer) session.getAttribute("employeeId");
        model.addAttribute("employees", projectService.getEmployeesNotOnProject(projectId));
        BigDecimal totalEstimate = projectService.getTotalEstimatedTimeForProject(1);
        model.addAttribute("project", project);
        model.addAttribute("mainTask", false);
        model.addAttribute("progressMap", taskService.getMainTaskProgress(projectId));
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("totalEstimate", totalEstimate);
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

    @PostMapping("/{projectId}/add-member-details")
    public String addProjectMemberInDetailsPage(@RequestParam int employeeId, @PathVariable int projectId){
        Employee employee = employeeService.getEmployeeById(employeeId);
        Project project = projectService.getProjectById(projectId);
        projectService.addProjectMemberToProject(employee, project);
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/{projectId}/remove-member")
    public String removeProjectMember(@RequestParam int employeeId, @PathVariable int projectId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        Project project = projectService.getProjectById(projectId);
        projectService.removeProjectMemberFromProject(employee, project);
        return "redirect:/projects";
    }

    @PostMapping("/archive")
    public String archiveProject(@RequestParam int projectId, RedirectAttributes attributes) {
        try {
            projectService.archiveProject(projectId);
            return "redirect:/projects";
        } catch (DatabaseOperationException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/projects/" + projectId;
        }
    }

    @PostMapping("/restore")
    public String restoreProject(@RequestParam int projectId, RedirectAttributes attributes) {
        try {
            projectService.restoreProject(projectId);
            return "redirect:/projects";
        } catch (DatabaseOperationException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/projects/" + projectId;
        }
    }

    @GetMapping("/{projectId}/edit")
    public String showEditProjectForm(@PathVariable int projectId, Model model){
        try{
            Project projectToEdit = projectService.getProjectById(projectId);
            model.addAttribute("project", projectToEdit);
            model.addAttribute("employeesNotOnProject", projectService.getEmployeesNotOnProject(projectId));
            model.addAttribute("projectMembers", projectService.getProjectMembersByProjectId(projectId));
            return "project/edit-project";
        }catch (NotFoundException e){
            return "redirect:/projects";
        }
    }

    @PostMapping("{projectId}/edit")
    public String saveEditedProject(@PathVariable int projectId, @ModelAttribute Project project){
        project.setId(projectId);
        projectService.editProject(project);
        return "redirect:/projects/" + projectId;
    }
}
