package it.smartcommunitylabdhub.modules.nefertem.models.specs.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "validate", entity = SpecEntity.TASK)
public class TaskValidateSpec extends TaskBaseSpec<TaskValidateSpec> {

    @JsonProperty("run_config")
    private Map<String, Object> runConfig;

    @Override
    protected void configureSpec(TaskValidateSpec taskValidateSpec) {
        super.configureSpec(taskValidateSpec);

        this.setRunConfig(taskValidateSpec.getRunConfig());

    }
}
