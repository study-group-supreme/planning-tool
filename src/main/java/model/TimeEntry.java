package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TimeEntry {
int id;
int employeeId;
int taskId;
LocalDateTime logginTime;
BigDecimal timeSpent;

    public TimeEntry(int id, int employeeId, int taskId, LocalDateTime logginTime, BigDecimal timeSpent) {
        this.id = id;
        this.employeeId = employeeId;
        this.taskId = taskId;
        this.logginTime = logginTime;
        this.timeSpent = timeSpent;
    }
    public TimeEntry(){

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

    public LocalDateTime getLogginTime() {
        return logginTime;
    }

    public void setLogginTime(LocalDateTime logginTime) {
        this.logginTime = logginTime;
    }

    public BigDecimal getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(BigDecimal timeSpent) {
        this.timeSpent = timeSpent;
    }
}
