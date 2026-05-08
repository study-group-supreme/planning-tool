package planningtool.model;

import java.time.LocalDate;
import java.util.List;

public class Project {
    private int id;
    private String title;
    private String description;
    private LocalDate timeOfCreation;
    private LocalDate deadline;
    private int projectCreatorId;
    private boolean isActive;
    private List<Employee> projectMembers;
    private List<Task> tasks;

    public Project(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(LocalDate timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getProjectCreatorId() {
        return projectCreatorId;
    }

    public void setProjectCreatorId(int projectCreatorId) {
        this.projectCreatorId = projectCreatorId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Employee> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(List<Employee> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
