package it.smartcommunitylabdhub.core.services.context.interfaces;

import it.smartcommunitylabdhub.core.models.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TaskContextService {

    Task createTask(String projectName, Task taskDTO);

    Page<Task> getAllTasksByProjectName(
            Map<String, String> filter,
            String projectName, Pageable pageable);

    Task getByProjectAndTaskUuid(
            String projectName, String uuid);

    Task updateTask(String projectName, String uuid,
                    Task taskDTO);

    Boolean deleteSpecificTaskVersion(String projectName, String uuid);
}
