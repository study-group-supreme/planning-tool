package planningtool.service;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planningtool.exception.BadRequestException;
import planningtool.exception.DatabaseOperationException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.model.TimeEntry;
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;
import planningtool.repository.TaskRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.taskRepository = taskRepository;
    }

    // TODO Might need more exception handling
    public Project getProjectById(int id) {
        try {
            return projectRepository.findProjectById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Project not found");
        }
    }

    @Transactional
    public Project createProject(Project project) {
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (project.getTitle().length() > 255) {
            throw new BadRequestException("Title cannot be longer than 255 characters");
        }
        if (project.getDescription() != null && project.getDescription().length() > 1080) {
            throw new BadRequestException("Description cannot be longer than 1080 characters");
        }
        if (project.getDeadline() != null && project.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Deadline must be in the future");
        }
        project.setTimeOfCreation(LocalDate.now());
        project.setActive(true);
        project.setProjectMembers(new ArrayList<>());
        try {
            Project createdProject = projectRepository.insertProject(project);
            addProjectMemberToProject(employeeRepository.findEmployeeById(project.getProjectCreatorId()), project);
            return createdProject;
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Project couldn't be created", e.getCause());
        }
    }


    public List<Task> getTasksByProjectId(int id) {
        try {
            return projectRepository.findTasksByProjectId(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No project found");
        }
    }
    public List<Task> getMainTasksByProjectId(int id){
        return projectRepository.findMainTasksByProjectId(id);
    }

    public List<Employee> getProjectMembersByProjectId(int projectId) {
        return projectRepository.findProjectMembersByProjectId(projectId);
    }

    public List<Integer> getProjectMemberIdsByProjectId(int projectId){
        List<Employee> projectMembers = getProjectMembersByProjectId(projectId);
        List<Integer> projectMemberIds = new ArrayList<>();
        for(Employee employee : projectMembers){
            projectMemberIds.add(employee.getId());
        }
        return projectMemberIds;
    }

    public List<Employee> getEmployeesNotOnProject(int projectId) {
        return employeeRepository.findEmployeesNotOnProject(projectId);
    }

    public List<Project> getProjectsByEmployeeId(int employeeId) {
        List<Project> projects = projectRepository.findProjectsByEmployeeId(employeeId);
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if (projects == null || projects.isEmpty()) {
            //Need help with good error message
            throw new NotFoundException("You are not connected to any projects ");

        }
        return projects;
    }

    public Project editProject(Project project) {
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (project.getTitle().length() > 225) {
            throw new BadRequestException("Task title cannot exceed 225 characters");
        }
        if ((project.getDescription() != null) && (project.getDescription().length() > 1080)){
            throw new BadRequestException("Project description cannot exceed 1080 characters");
        }
        if (project.getDeadline() != null && (project.getDeadline().isBefore(LocalDate.now()))){
            throw new BadRequestException("Deadline has to be in the future");
        }
        try {
            projectRepository.updateProject(project);
            return projectRepository.findProjectById(project.getId());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Project could not be updated", e.getCause());
        }
    }


    public Employee addProjectMemberToProject(Employee employee, Project project) {
        if (project.getProjectMembers().contains(employee)) {
            throw new BadRequestException("Employee already assigned to project");
        }
        try {
            projectRepository.insertProjectMember(employee, project);
            return employee;
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("Employee could not be added", e.getCause());
        }
    }

    public void removeProjectMemberFromProject(Employee employee, Project project) {
        if (!project.getProjectMembers().contains(employee)) {
            throw new NotFoundException("Employee: " + employee.getName() + " is not a member of this project");
        }
        try {
            projectRepository.deleteProjectMember(employee, project);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Employee could not be removed", e.getCause());
        }
    }

    public void archiveProject(int projectId) {
        try {
            projectRepository.archiveProject(projectId);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Project could not be found", e.getCause());
        }

    }

    public void restoreProject(int projectId) {
        try {
            projectRepository.restoreProject(projectId);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Project could not be found", e.getCause());
        }
    }

    @Transactional
    public BigDecimal getEstimatedTimeForTask(int taskId) {
        Task task = taskRepository.findTaskById(taskId);

        // Subtask → return its own estimate
        if (task.getParentTaskId() != null) {
            return task.getTimeEstimate();
        }

        // Parent task → check subtasks
        List<Task> subtasks = taskRepository.findSubtasksByParentId(taskId);

        if (subtasks.isEmpty()) {
            return task.getTimeEstimate();
        }

        // Parent with subtasks → sum subtasks
        BigDecimal total = BigDecimal.ZERO;
        for (Task st : subtasks) {
            if (st.getTimeEstimate() != null) {
                total = total.add(st.getTimeEstimate());
            }
        }
        taskRepository.updateTimeEstimateForTask(taskId, total);
        return total;
    }

    public BigDecimal getTotalEstimatedTimeForProject(int projectId) {
        List<Task> tasks = projectRepository.findMainTasksByProjectId(projectId);

        BigDecimal total = BigDecimal.ZERO;
        for (Task task : tasks){
            BigDecimal taskEstimate = getEstimatedTimeForTask(task.getId());
            if (taskEstimate != null){
                total = total.add(taskEstimate);
            }
        }
        return total;
    }

    public BigDecimal getLoggedTimeForTask(int taskId){
        // Always include the parent tasks own time entries
        BigDecimal total = sumTimeEntriesForTask(taskId);

        // It's a parent task --> sum all subtasks
        List<Task> subtasks = taskRepository.findSubtasksByParentId(taskId);

        // if no subtasks, return the tasks own sum
        if(subtasks.isEmpty()){
            return total;
        }

        for (Task subtask : subtasks){
            total = total.add(sumTimeEntriesForTask(subtask.getId()));
        }

        return total;
    }

    private BigDecimal sumTimeEntriesForTask(int taskId){
        List<TimeEntry> entries = taskRepository.findTimeEntriesByTaskId(taskId);
        BigDecimal total = BigDecimal.ZERO;
        for (TimeEntry entry : entries){
            if (entry.getTimeSpent() != null){
                total = total.add(entry.getTimeSpent());
            }
        }
        return total;
    }

    public BigDecimal getTotalLoggedTimeForProject(int projectId){
        List<Task> mainTasks = projectRepository.findMainTasksByProjectId(projectId);

        BigDecimal total = BigDecimal.ZERO;
        for(Task task : mainTasks) {
            BigDecimal loggedTime = getLoggedTimeForTask(task.getId());
            if (loggedTime != null){
                total = total.add(loggedTime);
            }
        }
        return total;
    }


    // Nested Map:
    // Outer map <Integer, ... > the keys represent the projectId
    // The inner map <String, BigDecimal> the keys are strings, "estimate", "logged"
    public Map<Integer, Map<String, BigDecimal>> getProjectTimeSummaries(List<Project> projects){
        Map<Integer, Map<String, BigDecimal>> result = new HashMap<>();

        for (Project project : projects){
            Map<String, BigDecimal> values = new HashMap<>();
            values.put("estimate", getTotalEstimatedTimeForProject(project.getId()));
            values.put("logged", getTotalLoggedTimeForProject(project.getId()));
            result.put(project.getId(), values);
        }
        return result;
    }

    @Transactional
    public BigDecimal calculateEstimatedPriceForTask(int taskId){
        Task task = taskRepository.findTaskById(taskId);
        Integer employeeId = task.getAssignedMemberId();

        return task.getTimeEstimate().multiply(employeeRepository.findPricePerHourByEmployeeId(employeeId));
    }

    @Transactional
    public BigDecimal calculateCurrentCostOfProject(int projectId){
        BigDecimal totalCost = BigDecimal.ZERO;
        for(Task task : projectRepository.findTasksByProjectId(projectId)){
            for(TimeEntry timeEntry : taskRepository.findTimeEntriesByTaskId(task.getId())){
                BigDecimal costOfTimeEntry = employeeRepository.findPricePerHourByEmployeeId(timeEntry.getEmployeeId()).multiply(timeEntry.getTimeSpent());
                totalCost = totalCost.add(costOfTimeEntry);
            }
        }
        return totalCost;
    }
}

