package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.FlashAttributeResultMatchers;
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
import java.util.ArrayList;
import java.util.List;

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
    void createProject_ShouldShowCreateProjectForm() throws Exception {
        mockMvc.perform(get("/projects/add").sessionAttr("employeeId", 1))
                .andExpect(status().isOk()).
                andExpect(view().name("project/create-project"))
                .andExpect(model().attributeExists("project"));
    }

    @Test
    void createProject_ShouldPostCreateFormAndRedirectToProjects() throws Exception {
        mockMvc.perform(post("/projects/add").sessionAttr("employeeId", 1).
                        param("title", "title")
                        .param("description", "description")
                        .param("deadline", "2028-02-02"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/projects/add-member/*"));
    }

    @Test
    void createProject_CatchesBadRequestExceptionWhenBadRequest() throws Exception {
        when(projectService.createProject(any())).thenThrow(new BadRequestException(""));

        mockMvc.perform(post("/projects/add")
                        .sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/add"))
                .andExpect(flash().attributeExists("error"));

    }

    @Test
    void showSpecificProject_ShouldShowSpecificProject() throws Exception {
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setActive(true);
        testProject.setTimeOfCreation(LocalDate.of(2026, 5, 22));

        when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(get("/projects/1")
                        .sessionAttr("employeeId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-project"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("isArchived"))
                .andExpect(model().attributeExists("progressMap"));

    }


    @Test
    void addProjectMember_ShouldAddProjectMember() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);

        Employee employee = new Employee();
        employee.setName("test");
        employee.setId(1);

        Project project = new Project();
        project.setTitle("test");
        project.setId(1);

        when(employeeService.getEmployeeById(1)).thenReturn(employee);
        when(projectService.getProjectById(1)).thenReturn(project);

        mockMvc.perform(post("/projects/{projectId}/add-member", 1)
                        .param("employeeId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/add-member/1"));
        verify(projectService).addProjectMemberToProject(employee, project);
    }

    @Test
    void removeProjectMember_ShouldRemoveProjectMember() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Employee employee = new Employee();
        employee.setName("test");
        employee.setId(1);

        Project project = new Project();
        project.setTitle("testProject");
        project.setId(1);

        when(employeeService.getEmployeeById(1)).thenReturn(employee);
        when(projectService.getProjectById(1)).thenReturn(project);

        mockMvc.perform(post("/projects/{projectId}/remove-member", 1)
                        .param("employeeId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
        verify(projectService).removeProjectMemberFromProject(employee, project);
    }


//TODO Test for showing showSpecificProject() should be added

    @Test
    void archiveProject_ShouldArchiveProject_AndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setTitle("test");

        Mockito.when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/projects/archive")
                        .param("projectId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

    }

    @Test
    void archiveProject_CatchesDatabaseOperationException_AddRedirectAttribute() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        doThrow(new DatabaseOperationException("", new Exception())).when(projectService).archiveProject(anyInt());

        mockMvc.perform(post("/projects/archive").param("projectId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void restoreProject_ShouldRestoreProject_AndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setTitle("test");

        Mockito.when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(post("/projects/restore")
                        .param("projectId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

    }

    @Test
    void restoreProject_CatchesDatabaseOperationException_AddRedirectAttribute() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        doThrow(new DatabaseOperationException("", new Exception())).when(projectService).restoreProject(anyInt());

        mockMvc.perform(post("/projects/restore").param("projectId", "1").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"))
                .andExpect(flash().attributeExists("error"));
    }


    @Test
    void showEditProjectForm_ShouldShowEditProjectForm() throws Exception {
        Project testProject = new Project();
        testProject.setId(1);
        Mockito.when(projectService.getProjectById(1)).thenReturn(testProject);

        mockMvc.perform(get("/projects/1/edit").sessionAttr("employeeId", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-project"))
                .andExpect(model().attribute("project", testProject));
    }

    @Test
    void showEditProjectForm_ShouldCatchNotFoundException() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Mockito.when(projectService.getProjectById(250)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/projects/250/edit").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    void saveEditedProject_ShouldSaveProjectAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        mockMvc.perform(post("/projects/1/edit")
                .param("title", "Updated title").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1"));
    }

    @Test
    void saveEditedProject_ShouldCatchBadRequestExceptionAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Mockito.when(projectService.editProject(any(Project.class))).thenThrow(BadRequestException.class);

        mockMvc.perform(post("/projects/1/edit")
                .param("title", "New title").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects/1/edit"));
    }

    @Test
    void saveEditedProjects_ShouldCatchDatabaseOperationException() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);
        Mockito.when(projectService.editProject(any(Project.class))).thenThrow(DatabaseOperationException.class);

        mockMvc.perform(post("/projects/1/edit")
                .param("title", "New title").sessionAttr("employeeId", 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

    }

    @Test
    void showAllProjects_ShouldShowAllProjects() throws Exception {
        Project testProject = new Project();
        testProject.setId(1);
        testProject.setTimeOfCreation(LocalDate.of(2026, 5, 22));
        List<Project> myProjects = new ArrayList<>();
        myProjects.add(testProject);

        when(projectService.getAllProjects()).thenReturn(myProjects);
        when(projectService.getTotalLoggedTimeForProject(1)).thenReturn(new BigDecimal("5.0"));

        mockMvc.perform(get("/projects/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list-project-history"))
                .andExpect(model().attributeExists("allProjects"))
                .andExpect(model().attributeExists("loggedTime"));
    }
}
