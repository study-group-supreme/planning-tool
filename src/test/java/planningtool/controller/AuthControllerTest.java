package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import planningtool.exception.BadRequestException;
import planningtool.model.Employee;
import planningtool.service.EmployeeService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    //TODO might need revision after LoginInterceptor is implemented
    //TODO Add ArgumentCaptors to tests

    //TODO if possible to test LoginInterceptor/WebConfig it should be in this test class

    @Test
    void showLoginForm_shouldDisplayLoginForm() throws Exception{
        mockMvc.perform(get("/auth/login")) // this refers to the controller endpoint
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login")); // This is the html template
    }

    @Test
    void loginFormHandler_shouldLoginAndRedirect() throws Exception {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Andreas Jensen");
        employee.setEmail("grey@email.com");
        employee.setPassword("1234");
        employee.setRoleId(2);

        when(employeeService.login("grey@email.com", "1234")).thenReturn(employee);

        mockMvc.perform(post("/auth/login")
                .param("email", "grey@email.com")
                .param("password", "1234"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(employeeService).login("grey@email.com", "1234");

    }

    @Test
    void logout_shouldInvalidateSessionAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("employeeId", 1);

        mockMvc.perform(get("/auth/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void login_showsError_whenCredentialsInvalid() throws Exception {
        when(employeeService.login("test@mail.com", "wrong"))
                .thenThrow(new BadRequestException("Incorrect password"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "test@mail.com")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", true))
                .andExpect(view().name("auth/login"));
    }

}
