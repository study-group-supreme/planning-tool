package planningtool.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.TaskRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertThat(task.getTimeEstimate()).isEqualByComparingTo("1.5");
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
    void insertTimeEntry_shouldCreateNewTimeEntry() {
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

    @Test
    void updateTask_shouldUpdateTask() {
        Task task = taskRepository.findTaskById(4);

        task.setParentTaskId(1);
        task.setAssignedMemberId(3);
        task.setTitle("Tell funny joke");
        task.setDescription("test");
        task.setTimeEstimate(new BigDecimal("1.5"));
        task.setHighPriority(true);
        task.setDone(true);

        taskRepository.updateTask(task);

        Task updatedTask = taskRepository.findTaskById(4);

        assertThat(updatedTask.getParentTaskId()).isEqualTo(1);
        assertThat(updatedTask.getAssignedMemberId()).isEqualTo(3);
        assertThat(updatedTask.getTitle()).isEqualTo("Tell funny joke");
        assertThat(updatedTask.getDescription()).isEqualTo("test");
        assertThat(updatedTask.getTimeEstimate()).isEqualByComparingTo(new BigDecimal("1.5"));
        assertTrue(updatedTask.isHighPriority());
        assertTrue(updatedTask.isDone());

    }

    @Test
    void deleteTaskById_ShouldDeleteTaskById() {
        Task task = new Task();
        task.setId(1);
        task.setProjectId(1);
        task.setTitle("test");

        taskRepository.deleteTaskById(1);

        assertThatThrownBy(() -> taskRepository.findTaskById(1))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void findSubtasksByParentId_shouldReturnListOfTasksWithSameParrentId() {
        List<Task> allSubTasks = taskRepository.findSubtasksByParentId(1);

        assertThat(allSubTasks.size()).isEqualTo(2);
        assertThat(allSubTasks.get(0).getTitle()).isEqualTo("Grind the beans");
    }

    @Test
    void updateIsDoneInTaskById_ShouldUpdateTaskToDoneStatus() {
        Task task = taskRepository.findTaskById(1);
        task.setDone(true);
        Task updatedTask = taskRepository.updateIsDoneInTaskById(task);
        assertThat(updatedTask.isDone()).isTrue();
    }

    @Test
    void updateIsDoneInTaskById_ShouldUpdateTaskToNotDoneStatus() {
        Task task = taskRepository.findTaskById(1);
        task.setDone(false);
        Task updatedTask = taskRepository.updateIsDoneInTaskById(task);
        assertThat(updatedTask.isDone()).isFalse();
    }

    @Test
    void taskHasChildren_ShouldReturnTrueWhenChildrenExist(){
        // task with id=1 has children
        boolean result = taskRepository.taskHasChildren(1);

        assertThat(result).isTrue();
    }

    @Test
    void taskHasChildren_shouldReturnFalseWhenNoChildren(){
        // task with id=4 has no children
        boolean result = taskRepository.taskHasChildren(4);

        assertThat(result).isFalse();
    }
}
