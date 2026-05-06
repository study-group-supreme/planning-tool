package planningtool.service;

import org.springframework.stereotype.Service;
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

    public Project createProject(Project project) {
        return projectRepository.insertProject(project);
    }

    public List<Task> getTaskByProjectId(int id) {
        return projectRepository.findTasksByProjectId(id);
    }

    public List<Employee> getProjectMembersByProjectId(int id) {
        return projectRepository.findProjectMembersByProjectId(id);
    }
}
