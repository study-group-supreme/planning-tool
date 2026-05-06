package planningtool.repository;

import planningtool.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class TaskRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Task> taskRowMapper = (rs, rowNum ) -> {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setProjectId(rs.getInt("project_id"));
        task.setParentTaskId(rs.getInt("parent_task_id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setTimeEstimate(rs.getBigDecimal("time_estimate"));
        task.setHighPriority(rs.getBoolean("is_high_priority"));
        return task;
    };

    public TaskRepository (JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Task findTaskById(int taskId) {
        String sql = """
                        SELECT task.id, task.project_id, task.parent_task_id, task.title, task.description, task.time_estimate, task.is_high_priority
                        FROM task
                        WHERE id = ?""";
        return jdbc.queryForObject(sql, taskRowMapper,taskId);
    }



}
