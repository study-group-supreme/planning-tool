package planningtool.repository;

import planningtool.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;


@Repository
public class TaskRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setProjectId(rs.getInt("project_id"));
        task.setParentTaskId((Integer) rs.getObject("parent_task_id",Integer.class));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setTimeEstimate(rs.getBigDecimal("time_estimate"));
        task.setHighPriority(rs.getBoolean("is_high_priority"));
        return task;
    };

    public TaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public RowMapper<Task> getTaskRowMapper() {
        return taskRowMapper;
    }

    public Task findTaskById(int taskId) {
        String sql = """
                SELECT *
                FROM task
                WHERE id = ?""";
        return jdbc.queryForObject(sql, taskRowMapper, taskId);
    }


}
