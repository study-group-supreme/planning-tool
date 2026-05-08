package planningtool.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planningtool.exception.BadRequestException;
import planningtool.exception.NotFoundException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.repository.EmployeeRepository;
import planningtool.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository=employeeRepository;
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
        if (project.getDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Deadline must be in the future");
        }//if(project.getDeadline == null) Throw new BadRequestException("...")
        project.setTimeOfCreation(LocalDate.now());
        project.setActive(true);
        return projectRepository.insertProject(project);
    }


    public List<Task> getTaskByProjectId(int id) {
        return projectRepository.findTasksByProjectId(id);
    }

    public List<Employee> getProjectMembersByProjectId(int id) {
        List<Employee> members = projectRepository.findProjectMembersByProjectId(id);
        if (members == null || members.isEmpty()) {
            //Need method to findProjectsById so we can call it insted of id in error message
            throw new NotFoundException("No project members found for this project" + id);
        }
        return members;
    }
    public List<Project> getProjectsByEmployeeId(int employeeId, Employee employee){
        List<Project> projects = projectRepository.findProjectsByEmployeeId(employeeId);
        if (projects == null || projects.isEmpty()){
            //Need help with good error message
            throw new NotFoundException("No projects found connected to " + employee.getName());

        }
        return projects;
    }
}

