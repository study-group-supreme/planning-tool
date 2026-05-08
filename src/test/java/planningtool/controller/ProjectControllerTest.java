package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    void ShowListOfProjectsByEmployeeId_ShouldReturnListOfProjectsByEmployeeId() throws Exception {
        mockMvc.perform(get("/projects").sessionAttr("employeeId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list-projects"));
    }
}
