package it.smartcommunitylabdhub.core.components.infrastructure.factories.builders;

import it.smartcommunitylabdhub.core.models.entities.function.FunctionDTO;
import it.smartcommunitylabdhub.core.models.entities.run.RunDTO;
import it.smartcommunitylabdhub.core.models.entities.task.TaskDTO;


/**
 * Given a function string a task and a executionDTO return a RunDTO
 */
public interface Builder {
	RunDTO build(FunctionDTO function, TaskDTO task, RunDTO inputRunDTO);
}
