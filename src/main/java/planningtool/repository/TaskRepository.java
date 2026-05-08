package planningtool.repository;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import planningtool.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;


@Repository
public class TaskRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setProjectId(rs.getInt("project_id"));
        task.setParentTaskId((Integer) rs.getObject("parent_task_id", Integer.class));
        task.setAssignedMemberId(rs.getInt("assigned_member_id"));
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

    public Task insertTask(Task task) {
        String sql = """
                INSERT INTO task(project_id, parent_task_id, assigned_member_id, title, description, time_estimate, is_high_priority)
                VALUES(?,?,?,?,?,?,?)
                """;
        KeyHolder keyholder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, task.getProjectId());
            ps.setInt(2, task.getParentTaskId());
            ps.setInt(3, task.getAssignedMemberId());
            ps.setString(4, task.getTitle());
            ps.setString(5, task.getDescription());
            ps.setBigDecimal(6, task.getTimeEstimate());
            ps.setBoolean(7, task.isHighPriority());
            return ps;
        }, keyholder);
        task.setId(keyholder.getKey().intValue());
        return task;
    }


    public Task findTaskById(int taskId) {
        String sql = """
                SELECT *
                FROM task
                WHERE id = ?""";
        return jdbc.queryForObject(sql, taskRowMapper, taskId);
    }


}
