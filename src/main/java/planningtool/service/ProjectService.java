package planningtool.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import planningtool.Exeption.BadRequestException;
import planningtool.model.Employee;
import planningtool.model.Project;
import planningtool.model.Task;
import planningtool.repository.ProjectRepository;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
@Transactional
    public Project createProject(Project project) {
        if (project.getTitle() == null || project.getTitle().isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (project.getDescription().length() > 1080) {
            throw new BadRequestException("Description cannot be longer than 1080 characters");
        }
        return projectRepository.insertProject(project);
    }


    public List<Task> getTaskByProjectId(int id) {
        return projectRepository.findTasksByProjectId(id);
    }

    public List<Employee> getProjectMembersByProjectId(int id) {
        return projectRepository.findProjectMembersByProjectId(id);
    }
}

