package planningtool.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;

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
        p.setDeadline((LocalDate) rs.getObject("deadline"));
        p.setProjectManagerId(rs.getInt("project_manager_id"));
        p.setTimeOfCreation((LocalDate) rs.getObject("time_of_creation"));
        p.setProjectMembers(findProjectMembersByProjectId(p.getId()));
        p.setTasks(findTasksByProjectId(p.getId()));
        return p;
    });

    public List<Employee> findProjectMembersByProjectId(int id) {
        String sql = """
                SELECT employee.id, employee.name, employee.is_project_manager, employee.email, employee.password
                FROM employee
                JOIN project_member
                ON employee.id = project_member.employee_id
                WHERE project_member.project_id = ?
                """;
        return jdbc.query(sql, employeeRepository.getEmployeeRowMapper(), id);
    }

    public List<Task> findTasksByProjectId(int id) {
        String sql = """
                SELECT task.id, task.title, task.description, task.time_estimate, task.is_high_priority,
                task.parent_task_id, task.project_id
                FROM task
                JOIN project
                ON task.project_id = project.id
                WHERE task.project_id = ?
                """;
        return jdbc.query(sql, taskRepository.getTaskRowMapper(), id);
    }
}
