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
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;
import planningtool.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<Employee> getProjectMembersByProjectId(int id) {
        return projectRepository.findProjectMembersByProjectId(id);
    }


    public List<Employee> getEmployeesNotOnProject(int projectId) {
        return employeeRepository.findEmployeesNotOnProject(projectId);
    }

    public List<Project> getProjectsByEmployeeId(int employeeId) {
        List<Project> projects = projectRepository.findProjectsByEmployeeId(employeeId);
        Employee employee = employeeRepository.findEmployeeById(employeeId);
        if (projects == null || projects.isEmpty()) {
            //Need help with good error message
            throw new NotFoundException("No projects found connected to " + employee.getName());

        }
        return projects;
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


}

