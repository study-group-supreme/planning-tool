package planningtool.model;

import java.math.BigDecimal;
import java.util.List;

public class Task {
    private int id;
    private int projectId;
    private Integer parentTaskId;
    private int assignedMemberId;
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

    public int getAssignedMemberId() {
        return assignedMemberId;
    }

    public void setAssignedMemberId(int assignedMemberId) {
        this.assignedMemberId = assignedMemberId;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
