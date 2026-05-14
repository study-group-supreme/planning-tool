package planningtool.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Task {
    private int id;
    private int projectId;
    private Integer parentTaskId;
    private Integer assignedMemberId;
    private String title;
    private String description;
    private BigDecimal timeEstimate;
    private List<TimeEntry> timeEntries;
    private boolean isHighPriority;
    private boolean isDone;

    public Task(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Integer getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
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

    public BigDecimal getTimeEstimate() {
        return timeEstimate;
    }

    public void setTimeEstimate(BigDecimal timeEstimate) {
        this.timeEstimate = timeEstimate;
    }

    public List<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        this.timeEntries = timeEntries;
    }

    public boolean isHighPriority() {
        return isHighPriority;
    }

    public void setHighPriority(boolean highPriority) {
        isHighPriority = highPriority;
    }

    public Integer getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(Integer assignedMemberId) {
        this.assignedMemberId = assignedMemberId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && projectId == task.projectId && isHighPriority == task.isHighPriority && isDone == task.isDone && Objects.equals(parentTaskId, task.parentTaskId) && Objects.equals(assignedMemberId, task.assignedMemberId) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects.equals(timeEstimate, task.timeEstimate) && Objects.equals(timeEntries, task.timeEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, projectId, parentTaskId, assignedMemberId, title, description, timeEstimate, timeEntries, isHighPriority, isDone);
    }
}
