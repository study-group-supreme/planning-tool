package planningtool.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import planningtool.model.Employee;

import java.util.List;

@Repository
public class EmployeeRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setPassword(rs.getString("password"));
        employee.setProjectManager(rs.getBoolean("is_project_manager"));
        return employee;
    };

    public EmployeeRepository(JdbcTemplate jdbc){
        this.jdbc = jdbc;
    }

    public RowMapper<Employee> getEmployeeRowMapper() {
        return employeeRowMapper;
    }

    public Employee findEmployeeByEmail(String email){
        String sql = "SELECT * FROM employee WHERE email = ?";
        return jdbc.queryForObject(sql, employeeRowMapper, email);
    }

    public Employee findEmployeeById(int id){
        String sql = "SELECT * FROM employee WHERE id = ?";
        return jdbc.queryForObject(sql, employeeRowMapper, id);
    }

    public List<Employee> findAllEmployees(){
        String sql = "SELECT * FROM employee";
        return jdbc.query(sql,employeeRowMapper);
    }
}
