package planningtool.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import planningtool.repository.TaskRepository;

@SpringBootTest
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskRepositoryTest {

    // TODO: add this back in when taskRepository actually contains something
//    @Autowired
//    private TaskRepository taskRepository;
}
