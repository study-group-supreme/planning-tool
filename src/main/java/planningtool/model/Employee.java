package planningtool.model;

import java.util.Objects;

public class Employee {

    private int id;
    private int roleId;
    private String name;
    private String email;
    private String password;

    public Employee(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null || getClass() != o.getClass()) return false;
//        Employee employee = (Employee) o;
//        return id == employee.id && roleId == employee.roleId && Objects.equals(name, employee.name) && Objects.equals(email, employee.email) && Objects.equals(password, employee.password);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, roleId, name, email, password);
//    }
}
