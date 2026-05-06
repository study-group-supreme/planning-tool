package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Task;
import planningtool.repository.TaskRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskRepositoryTest {

@Autowired
private TaskRepository taskRepository;

@Test
    void findTaskById_returnsCorrectTask(){
    Task task = taskRepository.findTaskById(1);

    assertThat(task.getTitle()).isEqualTo("Brew coffee");
    assertThat(task.isHighPriority()).isEqualTo(true);
    assertThat(task.getTimeEstimate()).isEqualByComparingTo("0.5");
    assertThat(task.getProjectId()).isEqualTo(1);
    assertThat(task.getParentTaskId()).isNull();
    assertThat(task.getDescription()).isNull();;

}
}
