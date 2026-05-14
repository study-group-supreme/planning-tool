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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertEquals;


import org.springframework.web.bind.annotation.RequestParam;
import planningtool.model.Employee;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    void showSpecificTask_ReturnsDetailsPage() throws Exception {
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

        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);

        verify(taskService, Mockito.times(2)).getTaskById(captor.capture());
        verify(employeeService).getEmployeeById(captor.capture());
        verify(taskService).getTimeEntriesByTaskId(captor.capture());
        verify(projectService).getProjectMembersByProjectId(captor.capture());

        List<Integer> captured = captor.getAllValues();

        assertThat(captured.get(0)).isEqualTo(10); // main task
        assertThat(captured.get(1)).isEqualTo(5);  // parent task
        assertThat(captured.get(2)).isEqualTo(3);  // assigned member
        assertThat(captured.get(3)).isEqualTo(10); // time entries
        assertThat(captured.get(4)).isEqualTo(1);  // project members
    }

    @Test
    void removeTask_ShouldRemoveTaskByTaskId_AndRedirect() throws Exception {
        Task task = new Task();
        task.setId(1);
        task.setProjectId(1);


        mockMvc.perform(post("/tasks/remove")
                        .param("taskId", "1")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));

        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(taskService).removeTaskById(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1);
    }

    @Test
    void editTaskIsDoneStatus_shouldEditIsDoneStatus_AndRedirect() throws Exception {
        Task task = new Task();
        task.setProjectId(1);
        task.setId(1);
        task.setDone(false);


        mockMvc.perform(post("/tasks/mark-done")
                        .param("taskId", "1")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);

        verify(taskService).editTaskIsDoneStatus(captor.capture());
        assertThat(captor.getValue()).isEqualTo(1);
    }

    @Test
    void quickSaveTask_ShouldCreateTask() throws Exception {
        Task task = new Task();
        task.setProjectId(1);
        task.setId(1);

        mockMvc.perform(post("/tasks/quick-add")
                        .param("title", "test")
                        .param("projectId", "1")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskService).createTask(captor.capture());

        Task createdTask = captor.getValue();
        assertEquals("test", createdTask.getTitle());
    }

    @Test
    void showAddTaskForm_ShouldShowCreateTaskForm() throws Exception {

        Employee testEmployee = new Employee();
        testEmployee.setId(1);
        testEmployee.setName("John Doe");
        testEmployee.setEmail("random@email.com");

        List<Employee> testList = new ArrayList<>();
        testList.add(testEmployee);

        Task testTask = new Task();
        testTask.setProjectId(1);

        Mockito.when(projectService.getProjectMembersByProjectId(1)).thenReturn(testList);

        mockMvc.perform(get("/tasks/add?projectId=1").sessionAttr("employeeId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("task/create-task"))
                .andExpect(model().attribute("members", testList))
                .andExpect(model().attribute("task", testTask));

    }

//TODO This test has not been completely overseen by someone more capable than me lol
    @Test
    void saveTask_ShouldCreateTaskAndRedirect() throws Exception {

        mockMvc.perform(post(("/tasks/add")).param("title", "Brew coffee").param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));

    }


    //TODO showAddTaskForm() tests should be added - DONE
    //TODO saveTask() tests should be made - DONE?
    @Test
    void submitTimeEntry_ShouldCreateEntryAndRedirect() throws Exception{
        int taskId = 5;

        mockMvc.perform(post("/tasks/{taskId}/time-entry", taskId)
                        .param("timeSpent", "1.5")
                        .sessionAttr("employeeId", 3))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/" + taskId));

        ArgumentCaptor<TimeEntry> captor = ArgumentCaptor.forClass(TimeEntry.class);
        Mockito.verify(taskService).createTimeEntry(captor.capture());

        TimeEntry sent = captor.getValue();
        assertEquals(new BigDecimal("1.5"), sent.getTimeSpent());
        assertEquals(3,sent.getEmployeeId());
    }

    //TODO showAddTaskForm() tests should be added
    //TODO saveTask() tests should be made
    //TODO quickSaveTask() tests should be made
    //TODO removeTask() tests should be made
    //TODO editTaskIsDoneSTatus() tests should be made

    @Test
    void removeTimeEntry_ShouldCallServiceAndRedirect() throws Exception {
        int taskId = 7;
        int entryId = 3;

        mockMvc.perform(post("/tasks/{taskId}/time-entry/{entryId}/remove", taskId, entryId)
                .sessionAttr("employeeId", 10))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"+taskId));

        verify(taskService).removeTimeEntryById(entryId);
    }

}
