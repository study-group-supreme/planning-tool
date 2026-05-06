package planningtool.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import planningtool.repository.EmployeeRepository;

@SpringBootTest
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeRepositoryTest {

    // TODO: add this back in when employeeRepo contains something
//    @Autowired
//    private EmployeeRepository employeeRepository;
}
