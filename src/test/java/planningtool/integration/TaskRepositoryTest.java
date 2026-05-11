package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
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

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void findTaskById_returnsCorrectTask() {
        Task task = taskRepository.findTaskById(1);

        assertThat(task.getTitle()).isEqualTo("Brew coffee");
        assertThat(task.isHighPriority()).isEqualTo(true);
        assertThat(task.getAssignedMemberId()).isEqualTo(2);
        assertThat(task.getTimeEstimate()).isEqualByComparingTo("0.5");
        assertThat(task.getProjectId()).isEqualTo(1);
        assertThat(task.getParentTaskId()).isNull();
        assertThat(task.getDescription()).isNull();
        assertThat(task.isDone()).isFalse();
        ;
    }

    @Test
    void insertTask_ShouldCreateNewTask() {
        Task newTask = new Task();
        newTask.setProjectId(1);
        newTask.setParentTaskId(2);
        newTask.setAssignedMemberId(1);
        newTask.setTitle("Stack blank papers in a neat pile");
        newTask.setDescription("Extra focused on no corners poking out whatsoever");
        newTask.setTimeEstimate(new BigDecimal("0.25"));
        newTask.setHighPriority(true);
        newTask.setDone(false);

        Task result = taskRepository.insertTask(newTask);

        assertThat(result.getProjectId()).isEqualTo(1);
        assertThat(result.getParentTaskId()).isEqualTo(2);
        assertThat(result.getAssignedMemberId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Stack blank papers in a neat pile");
        assertThat(result.getDescription()).isEqualTo("Extra focused on no corners poking out whatsoever");
        assertThat(result.getTimeEstimate()).isEqualByComparingTo("0.25");
        assertThat(result.isHighPriority()).isEqualTo(true);
        assertThat(result.isDone()).isFalse();
    }

    @Test
    void insertTimeEntry_shouldCreateNewTimeEntry(){
        TimeEntry entry = new TimeEntry();
        entry.setEmployeeId(1);
        entry.setTaskId(2);
        entry.setTimeSpent(new BigDecimal("1.5"));

        TimeEntry saved = taskRepository.insertTimeEntry(entry);

        assertThat(saved.getId()).isGreaterThan(0);
        assertThat(saved.getEmployeeId()).isEqualTo(1);
        assertThat(saved.getTaskId()).isEqualTo(2);
        assertThat(saved.getTimeSpent()).isEqualByComparingTo("1.5");
    }

    @Test
    void deleteTimeEntryById_removesExistingEntry() {
        // verify entry id=1 exists in h2init
        Integer before = jdbc.queryForObject(
                "SELECT COUNT(*) FROM time_entry WHERE id = 1",
                Integer.class
        );
        assertThat(before).isEqualTo(1);

        taskRepository.deleteTimeEntryById(1);

        Integer after = jdbc.queryForObject(
                "SELECT COUNT(*) FROM time_entry WHERE id = 1",
                Integer.class
        );
        assertThat(after).isEqualTo(0);
    }
}
