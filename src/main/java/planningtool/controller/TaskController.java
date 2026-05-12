package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
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

    @PostMapping("/remove")
    public String removeTask(@RequestParam int taskId) {
        taskService.removeTaskById(taskId);

        return "redirect:/projects";
    }
    // TODO: finish designing task overview and apply time entries as needed
    // use below for inspo
//    @GetMapping("/{taskId}/time-entry")
//    public String showTimeEntryForm(@PathVariable int taskId, Model model) {
//        TimeEntry entry = new TimeEntry();
//        entry.setTaskId(taskId);
//        model.addAttribute("timeEntry", entry);
//        return "task/add-time-entry";
//    }
//
//    @PostMapping("/{taskId}/time-entry")
//    public String submitTimeEntry(
//            @PathVariable int taskId,
//            @ModelAttribute("timeEntry") TimeEntry timeEntry,
//            HttpSession session,
//            Model model
//    ) {
//        Integer employeeId = (Integer) session.getAttribute("employeeId");
//        timeEntry.setEmployeeId(employeeId);
//        timeEntry.setTaskId(taskId);
//
//        try {
//            taskService.createTimeEntry(timeEntry);
//            return "redirect:/tasks/" + taskId; // TODO: have a task detail page exist for this to work
//        } catch (Exception e){
//            model.addAttribute("error", e.getMessage());
//            return "task/add-time-entry";
//        }
//    }


}
