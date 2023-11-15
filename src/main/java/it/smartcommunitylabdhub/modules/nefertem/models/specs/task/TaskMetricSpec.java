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
@SpecType(kind = "metric", entity = SpecEntity.TASK)
public class TaskMetricSpec extends TaskBaseSpec<TaskMetricSpec> {

    @JsonProperty("run_config")
    private Map<String, Object> runConfig;

    @Override
    protected void configureSpec(TaskMetricSpec taskMetricSpec) {

        super.configureSpec(taskMetricSpec);

        this.setRunConfig(taskMetricSpec.getRunConfig());

    }
}
