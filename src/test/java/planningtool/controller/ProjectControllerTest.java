package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.service.EmployeeService;
import planningtool.service.ProjectService;
import planningtool.service.TaskService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    ProjectService projectService;
    @MockitoBean
    TaskService taskService;
    @MockitoBean
    EmployeeService employeeService;

    //TODO Add ArgumentCaptors to tests

    @Test
    void createProject_shouldShowCreateProjectForm() throws Exception {
        mockMvc.perform(get("/projects/add").sessionAttr("employeeId", 1))
                .andExpect(status().isOk()).
                andExpect(view().name("project/create-project"))
                .andExpect(model().attributeExists("project"));
    }

    @Test
    void createProject_shouldPostCreateFormAndRedirectToProjects() throws Exception {
        mockMvc.perform(post("/projects/add").sessionAttr("employeeId", 1).
                        param("title", "title")
                        .param("description", "description")
                        .param("deadline", "2028-02-02"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/projects/add-member/*"));
    }

    @Test
    void showListOfProjectsByEmployeeId_ShouldReturnListOfProjectsByEmployeeId() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Employee employee = new Employee();
        employee.setName("Test");
        when(employeeService.getEmployeeById(1)).thenReturn(employee);
        mockMvc.perform(get("/projects").sessionAttr("employeeId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list-projects"));
    }

    @Test
    void archiveProject_ShouldArchiveProject_AndRedirect() throws Exception {
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setTitle("test");

        Mockito.when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/projects/archive")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

    }

    @Test
    void restoreProject_ShouldRestoreProject_AndRedirect() throws Exception {
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setTitle("test");

        Mockito.when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/projects/restore")
                        .param("projectId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

    }

    //TODO Test for showing showSpecificProject() should be added
}
