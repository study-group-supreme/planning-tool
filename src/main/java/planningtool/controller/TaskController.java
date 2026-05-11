package planningtool.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import planningtool.model.TimeEntry;
import planningtool.service.TaskService;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
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
