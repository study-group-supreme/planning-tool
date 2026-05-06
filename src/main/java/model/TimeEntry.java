package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TimeEntry {
    int id;
    int employeeId;
    int taskId;
    LocalDateTime timeOfCreation;
    BigDecimal timeSpent;

    public TimeEntry(int id, int employeeId, int taskId, LocalDateTime timeOfCreation, BigDecimal timeSpent) {
        this.id = id;
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.timeOfCreation = timeOfCreation;
        this.timeSpent = timeSpent;
    }

    public TimeEntry() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(LocalDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public BigDecimal getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(BigDecimal timeSpent) {
        this.timeSpent = timeSpent;
    }
}
