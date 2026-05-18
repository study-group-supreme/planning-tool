package planningtool.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

@Repository

public class ProjectRepository {
    private JdbcTemplate jdbc;
    private TaskRepository taskRepository;
    private EmployeeRepository employeeRepository;

    public ProjectRepository(JdbcTemplate jdbc, TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.jdbc = jdbc;
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }


    private final RowMapper<Project> projectRowMapper = ((rs, rowNum) -> {
        Project p = new Project();
        p.setId(rs.getInt("id"));
        p.setTitle(rs.getString("title"));
        p.setDescription(rs.getString("description"));
        p.setActive(rs.getBoolean("active"));
        Date sqlDeadline = rs.getDate("deadline");
        if(sqlDeadline != null){
            p.setDeadline(sqlDeadline.toLocalDate());
        }
        p.setProjectCreatorId(rs.getInt("project_creator_id"));
        p.setTimeOfCreation(rs.getDate("time_of_creation").toLocalDate());
        p.setProjectMembers(findProjectMembersByProjectId(p.getId()));
        p.setTasks(findTasksByProjectId(p.getId()));
        return p;
    });


    // TODO Consider if this method should be moved to the EmployeeRepository
    public List<Employee> findProjectMembersByProjectId(int id) {
        String sql = """
                SELECT employee.id, employee.name, employee.role_id, employee.email, employee.password
                FROM employee
                JOIN project_member
                ON employee.id = project_member.employee_id
                WHERE project_member.project_id = ?
                ORDER BY employee.id
                """;
        return jdbc.query(sql, employeeRepository.getEmployeeRowMapper(), id);
    }

    public List<Task> findTasksByProjectId(int id) {
        String sql = """
                SELECT task.id, task.title, task.description, task.time_estimate, task.is_high_priority,
                task.parent_task_id, task.project_id, task.assigned_member_id,task.is_done
                FROM task
                JOIN project
                ON task.project_id = project.id
                WHERE task.project_id = ?
                """;
        return jdbc.query(sql, taskRepository.getTaskRowMapper(), id);
    }
    public List<Task> findMainTasksByProjectId(int id) {
        String sql = """
                SELECT task.id, task.title, task.description, task.time_estimate, task.is_high_priority,
                task.parent_task_id, task.project_id, task.assigned_member_id,task.is_done
                FROM task
                JOIN project
                ON task.project_id = project.id
                AND task.parent_task_id is null
                WHERE task.project_id = ?
                """;
        return jdbc.query(sql, taskRepository.getTaskRowMapper(), id);
    }

    public Project insertProject(Project project) {
        String sql = """
                INSERT INTO project(title, description, time_of_creation, project_creator_id, deadline, active)
                VALUES(?,?,?,?,?,?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, project.getTitle());
            ps.setString(2, project.getDescription());
            ps.setObject(3, project.getTimeOfCreation());
            ps.setInt(4, project.getProjectCreatorId());
            ps.setObject(5, project.getDeadline());
            ps.setBoolean(6, project.isActive());
            return ps;
        }, keyHolder);
        project.setId(keyHolder.getKey().intValue());
        return project;
    }

    public void updateProject(Project project) {
        String sql = """
                UPDATE project
                SET title = ?, description = ?, deadline = ?
                WHERE id = ?
                """;
        jdbc.update(sql, project.getTitle(), project.getDescription(), project.getDeadline(), project.getId());
    }

    public Project findProjectById(int id) {
        String sql = "SELECT * FROM project WHERE id = ?";
        return jdbc.queryForObject(sql, projectRowMapper, id);
    }

    public List<Project> findProjectsByEmployeeId(int employeeId) {
        String sql = """
                SELECT project.id, project.title, project.description, project.time_of_creation, project.project_creator_id, project.deadline, project.active
                FROM project
                JOIN project_member
                ON project_member.project_id = project.id
                WHERE project_member.employee_id = ? 
                """;
        return jdbc.query(sql, projectRowMapper, employeeId);
    }

    public void insertProjectMember(Employee employee, Project project) {
        String sql = """
                INSERT INTO project_member (employee_id, project_id)
                VALUES(?, ?) 
                """;
        jdbc.update(sql,employee.getId(), project.getId());
    }

    public void deleteProjectMember(Employee employee, Project project){
        String sql = "DELETE FROM project_member WHERE project_id = ? AND employee_id = ?";
        jdbc.update(sql, project.getId(), employee.getId());
    }

}
