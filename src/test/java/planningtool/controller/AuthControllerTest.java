package planningtool.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import planningtool.model.Employee;
import planningtool.service.EmployeeService;

import static org.junit.jupiter.api.Assertions.assertNull;
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
        employee.setProjectManager(true);

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
        mockMvc.perform(get("/auth/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

}
