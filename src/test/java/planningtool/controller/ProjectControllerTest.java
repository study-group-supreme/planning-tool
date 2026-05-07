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
    void createProject_shouldShowCreateProjectForm() throws Exception{
    mockMvc.perform(get("/projects/add").sessionAttr("employeeId", 1))
            .andExpect(status().isOk()).
            andExpect(view().name("project/create-project"))
            .andExpect(model().attributeExists("project"));

}

}
