package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import planningtool.model.Employee;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private EmployeeService employeeService;

    //TODO Add ArgumentCaptors to tests

    @Test
    void showSpecificTask_returnsDetailsPage() throws Exception {
        Task task = new Task();
        task.setId(10);
        task.setTitle("Brew Coffee");
        task.setDescription("Make a fresh pot");
        task.setProjectId(1);
        task.setAssignedMemberId(3);
        task.setParentTaskId(5);

        Task parent = new Task();
        parent.setId(5);
        parent.setTitle("Morning Routine");

        Employee assigned = new Employee();
        assigned.setId(3);
        assigned.setName("Daniella");

        TimeEntry entry = new TimeEntry();
        entry.setId(3);
        entry.setEmployeeId(3);
        entry.setTimeSpent(new BigDecimal("0.5"));
        entry.setTimeOfCreation(LocalDateTime.of(2026, 5, 10, 12, 0));

        List<Employee> projectMembers = List.of(assigned);

        Mockito.when(taskService.getTaskById(10)).thenReturn(task);
        Mockito.when(taskService.getTaskById(5)).thenReturn(parent);
        Mockito.when(employeeService.getEmployeeById(3)).thenReturn(assigned);
        Mockito.when(taskService.getTimeEntriesByTaskId(10)).thenReturn(List.of(entry));
        Mockito.when(projectService.getProjectMembersByProjectId(1)).thenReturn(projectMembers);

        mockMvc.perform(get("/tasks/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/details-task"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("assignedEmployee"))
                .andExpect(model().attributeExists("parentTask"))
                .andExpect(model().attributeExists("timeEntries"))
                .andExpect(model().attributeExists("projectMembers"));
    }

    @Test
    void removeTask_ShouldRemoveTaskByTaskId_AndRedirect() throws Exception {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Task task = new Task();
        task.setId(1);
        task.setProjectId(1);


        mockMvc.perform(post("/tasks/remove")
                        .param("taskId", "1")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));

        verify(taskService).removeTaskById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1);
    }

    @Test
    void editTaskIsDoneStatus_shouldEditIsDoneStatus_AndRedirect() throws Exception {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        Task task = new Task();
        task.setProjectId(1);
        task.setId(1);
        task.setDone(false);



        mockMvc.perform(post("/tasks/mark-done")
                        .param("taskId", "1")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
        verify(taskService).editTaskIsDoneStatus(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1);
    }

    //TODO showAddTaskForm() tests should be added
    //TODO saveTask() tests should be made
    //TODO quickSaveTask() tests should be made


}
