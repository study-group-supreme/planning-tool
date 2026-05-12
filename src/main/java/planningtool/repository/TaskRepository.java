package planningtool.repository;

import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import planningtool.model.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import planningtool.model.TimeEntry;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;


@Repository
public class TaskRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Task> taskRowMapper = (rs, rowNum) -> {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setProjectId(rs.getInt("project_id"));
        task.setParentTaskId((Integer) rs.getObject("parent_task_id", Integer.class));
        task.setAssignedMemberId((Integer) rs.getObject("assigned_member_id", Integer.class));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setTimeEstimate(rs.getBigDecimal("time_estimate"));
        task.setHighPriority(rs.getBoolean("is_high_priority"));
        task.setDone(rs.getBoolean("is_done"));
        return task;
    };

    private final RowMapper<TimeEntry> timeEntryRowMapper = (rs, rowNum) -> {
        TimeEntry entry = new TimeEntry();
        entry.setId(rs.getInt("id"));
        entry.setEmployeeId(rs.getInt("employee_id"));
        entry.setTaskId(rs.getInt("task_id"));
        entry.setTimeOfCreation(rs.getTimestamp("time_of_creation").toLocalDateTime());
        entry.setTimeSpent(rs.getBigDecimal("time_spent"));
        return entry;
    };

    public TaskRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public RowMapper<Task> getTaskRowMapper() {
        return taskRowMapper;
    }

    public RowMapper<TimeEntry> getTimeEntryRowMapper() {
        return timeEntryRowMapper;
    }

    public Task insertTask(Task task) {
        String sql = """
                INSERT INTO task(project_id, parent_task_id, assigned_member_id, title, description, time_estimate, is_high_priority, is_done)
                VALUES(?,?,?,?,?,?,?,?)
                """;
        KeyHolder keyholder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, task.getProjectId());
            ps.setObject(2, task.getParentTaskId(), Types.INTEGER);
            ps.setObject(3, task.getAssignedMemberId(), Types.INTEGER);
            ps.setString(4, task.getTitle());
            ps.setString(5, task.getDescription());
            ps.setBigDecimal(6, task.getTimeEstimate());
            ps.setBoolean(7, task.isHighPriority());
            ps.setBoolean(8, task.isDone());
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

    public TimeEntry insertTimeEntry(TimeEntry timeEntry) {
        String sql = """
                INSERT INTO time_entry(employee_id, task_id, time_spent) 
                VALUES (?,?,?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
            ps.setInt(1, timeEntry.getEmployeeId());
            ps.setInt(2, timeEntry.getTaskId());
            ps.setBigDecimal(3, timeEntry.getTimeSpent());
            return ps;
        }, keyHolder);
        timeEntry.setId(keyHolder.getKey().intValue());
        return timeEntry;
    }

    public List<TimeEntry> findTimeEntriesByTaskId(int taskId) {
        String sql = """
                SELECT id, employee_id, task_id, time_of_creation, time_spent
                FROM time_entry
                WHERE task_id = ?
                ORDER BY time_of_creation ASC
                """;
        return jdbc.query(sql, timeEntryRowMapper, taskId);
    }

    public void deleteTimeEntryById(int id) {
        String sql = "DELETE FROM time_entry WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void updateTask(Task task) {
        String sql = """
                UPDATE task
                SET parent_task_id = ?, assigned_member_id = ?, title = ?, description = ?, time_estimate = ?, is_high_priority = ?, is_done = ?
                """;
        jdbc.update(sql, task.getParentTaskId(), task.getAssignedMemberId(), task.getTitle(), task.getDescription(), task.getTimeEstimate(), task.isHighPriority(), task.isDone());
    }

    public void deleteTaskById(int id) {
        String sql = """
                DELETE FROM task WHERE id = ?
                """;
        jdbc.update(sql, id);
    }


}
