package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Task;
import planningtool.repository.TaskRepository;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findTaskById_returnsCorrectTask() {
        Task task = taskRepository.findTaskById(1);

        assertThat(task.getTitle()).isEqualTo("Brew coffee");
        assertThat(task.isHighPriority()).isEqualTo(true);
        assertThat(task.getTimeEstimate()).isEqualByComparingTo("0.5");
        assertThat(task.getProjectId()).isEqualTo(1);
        assertThat(task.getParentTaskId()).isNull();
        assertThat(task.getDescription()).isNull();
        ;
    }

    @Test
    void insertTask_ShouldCreateNewTask() {
        Task newTask = new Task();
        newTask.setProjectId(1);
        newTask.setParentTaskId(2);
        newTask.setTitle("Crack a funny joke during lunch break");
        newTask.setDescription("Raunchy knock-knock joke");
        newTask.setTimeEstimate(new BigDecimal("0.25"));
        newTask.setHighPriority(true);

        Task result = taskRepository.insertTask(newTask);

        assertThat(result.getProjectId()).isEqualTo(1);
        assertThat(result.getParentTaskId()).isEqualTo(2);
        assertThat(result.getTitle()).isEqualTo("Crack a funny joke during lunch break");
        assertThat(result.getDescription()).isEqualTo("Raunchy knock-knock joke");
        assertThat(result.getTimeEstimate()).isEqualByComparingTo("0.25");
        assertThat(result.isHighPriority()).isEqualTo(true);

    }
}
