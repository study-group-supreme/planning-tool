package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.EmployeeRepository;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.math.BigDecimal;


@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public TaskController(TaskService taskService, ProjectService projectService, EmployeeService employeeService) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{taskId}")
    public String showSpecificTask(@PathVariable int taskId, Model model, HttpSession session) {
        Integer loggedInId = (Integer) session.getAttribute("employeeId");
        Task task = taskService.getTaskById(taskId);

        Task parentTask = null;
        Employee employee = null;

        if (task.getParentTaskId() != null) {
            parentTask = taskService.getTaskById(task.getParentTaskId());
        }

        if (task.getAssignedMemberId() != null) {
            employee = employeeService.getEmployeeById((task.getAssignedMemberId()));
        }

        model.addAttribute("task", task);
        model.addAttribute("totalEstimatedTime", taskService.getEstimatedTime(taskId));
        model.addAttribute("timeEntries", taskService.getTimeEntriesByTaskId(taskId));
        model.addAttribute("projectMembers", projectService.getProjectMembersByProjectId(task.getProjectId()));
        model.addAttribute("assignedEmployee", employee);
        model.addAttribute("parentTask", parentTask);
        model.addAttribute("hasChildren", taskService.hasChildren(taskId));
        model.addAttribute("loggedInId", loggedInId);
        model.addAttribute("subtasks", taskService.getTasksByParentId(taskId));
        model.addAttribute("mainTask", false);
        return "task/details-task";
    }

    @GetMapping("/add")
    public String showAddTaskForm(Model model, @RequestParam int projectId) {
        Task task = new Task();
        task.setProjectId(projectId);
        model.addAttribute("members", projectService.getProjectMembersByProjectId(projectId));
        model.addAttribute("task", task);
        return "task/create-task";
    }

    @PostMapping("/add")
    public String saveTask(@ModelAttribute Task task) {
        taskService.createTask(task);
        return "redirect:/projects/" + task.getProjectId();
    }

    @PostMapping("/quick-add")
    public String quickSaveTask(
            @RequestParam int projectId,
            @RequestParam String title,
            HttpSession session) {

        Task task = new Task();
        task.setProjectId(projectId);
        task.setTitle(title);

        taskService.createTask(task);

        return "redirect:/projects/" + projectId;
    }

    // TODO We need to display an error message, right now it just redirects to prevent whitelabel

    @GetMapping("/{taskId}/add-subtask")
    public String showAddSubtaskForm(@PathVariable int taskId, Model model) {
        Task parent = taskService.getTaskById(taskId);

        Task subtask = new Task();

        subtask.setProjectId(parent.getProjectId());
        subtask.setParentTaskId(parent.getId());

        model.addAttribute("task", subtask);
        model.addAttribute("members", projectService.getProjectMembersByProjectId(parent.getProjectId()));

        return "task/create-task";
    }

    @PostMapping("/{taskId}/quick-add-subtask")
    public String quickAddSubtask(
            @PathVariable int taskId,
            @RequestParam String title
    ){
        Task parent = taskService.getTaskById(taskId);

        Task subTask = new Task();

        subTask.setProjectId(parent.getProjectId());
        subTask.setParentTaskId(parent.getId());
        subTask.setTitle(title);

        taskService.createTask(subTask);

        return "redirect:/tasks/"+taskId;
    }
// TODO Add error handling and try/catch to this? Sensei, help me!!
    @GetMapping("/{taskId}/edit")
    public String editTask(@PathVariable int taskId, Model model) {
        try {
            Task updatedTask = taskService.getTaskById(taskId);
            model.addAttribute("task", updatedTask);
            model.addAttribute("members", projectService.getProjectMembersByProjectId(updatedTask.getProjectId()));
            model.addAttribute("mainTask", projectService.getMainTasksByProjectId(updatedTask.getProjectId()));
            return "task/edit-task";
        } catch (NotFoundException e) {
            return "redirect:/projects";
        }
    }

    // TODO We need to display an error message, right now it just redirects to prevent whitelabel
    @PostMapping("/{taskId}/edit")
    public String saveEditedTask(@PathVariable int taskId, @ModelAttribute Task task) {
        try {
            task.setId(taskId);
            taskService.editTask(task);
            return "redirect:/tasks/" + taskId;
        } catch (BadRequestException e) {
            return "redirect:/tasks/" + taskId + "/edit";
        } catch (DatabaseOperationException e) {
            return "redirect:/projects";
        }
    }

    @PostMapping("/remove")
    public String removeTask(@RequestParam int taskId, @RequestParam int projectId, Model model) {
        try {
            taskService.removeTaskById(taskId);
        } catch (BadRequestException e) {
            model.addAttribute("mainTask", true);
            model.addAttribute("project", projectService.getProjectById(projectId));
            model.addAttribute("message", e.getMessage());
            return "project/details-project";

        }
        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/mark-done")
    public String editTaskIsDoneStatus(@RequestParam int taskId, @RequestParam int projectId) {
        taskService.editTaskIsDoneStatus(taskId);
        return "redirect:/projects/" + projectId;
    }

    // ATTN: NEEDS A LOGGED IN SESSION TO WORK
    @PostMapping("/{taskId}/time-entry")
    public String submitTimeEntry(
            @PathVariable int taskId,
            @RequestParam BigDecimal timeSpent,
            HttpSession session,
            Model model
    ) {
        Integer employeeId = (Integer) session.getAttribute("employeeId");

        TimeEntry entry = new TimeEntry();
        entry.setTaskId(taskId);
        entry.setEmployeeId(employeeId);
        entry.setTimeSpent(timeSpent);

        taskService.createTimeEntry(entry);
        return "redirect:/tasks/" + taskId;
    }

    @PostMapping("/{taskId}/time-entry/{entryId}/remove")
    public String removeTimeEntry(
            @PathVariable int taskId,
            @PathVariable int entryId,
            HttpSession session
    ) {
        Integer employeeId = (Integer) session.getAttribute("employeeId");
        taskService.removeTimeEntryById(entryId);
        return "redirect:/tasks/" + taskId;
    }

}
