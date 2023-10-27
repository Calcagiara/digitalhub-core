/**
 * BuildTaskBuilder.java
 * <p>
 * This class implements the KindBuilder interface to build a TaskDTO for the specific kind "build".
 */

package it.smartcommunitylabdhub.core.components.kinds;

import it.smartcommunitylabdhub.core.annotations.olders.RunBuilderComponent;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.kinds.factory.builders.KindBuilder;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.task.TaskSpecBuilder;
import it.smartcommunitylabdhub.core.models.entities.task.TaskDTO;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskSpec;
import it.smartcommunitylabdhub.core.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RunBuilderComponent(platform = "build", perform = "perform")
public class BuildTaskBuilder extends TaskSpecBuilder implements KindBuilder<TaskDTO, TaskDTO> {
        // Implementation of the builder

        @Autowired
        SpecRegistry<? extends Spec> specRegistry;

        @Autowired
        TaskRepository taskRepository;

        /**
         * Build a TaskDTO for the "build" kind using the input TaskDTO.
         *
         * @param taskDTO The input TaskDTO to be processed for building the TaskDTO.
         * @return The built TaskDTO of the "build" kind.
         * @throws CoreException If the accessor for the TaskDTO is not found, leading to a failure
         *         in building the TaskDTO.
         */
        @Override
        public TaskDTO build(TaskDTO taskDTO) {
                // Use TaskUtils to parse the task and create the TaskAccessor

                TaskSpec taskSpec =
                                (TaskSpec) specRegistry.createSpec(taskDTO.getKind(),
                                                taskDTO.getSpec());

                return Optional.of(TaskUtils.parseTask(taskSpec.getFunction()))
                                .map(accessor -> {
                                        // Build the TaskDTO with the parsed task data
                                        TaskDTO dto = TaskDTO.builder().kind(taskDTO.getKind())
                                                        .project(taskDTO.getProject())
                                                        .spec(taskDTO.getSpec()).build();
                                        dto.getSpec().putAll(getBaseSpec(taskSpec));
                                        return dto;
                                })
                                .orElseThrow(() -> new CoreException("TaskAccessorNotFound",
                                                "Cannot create accessor",
                                                HttpStatus.INTERNAL_SERVER_ERROR));

        }
}
