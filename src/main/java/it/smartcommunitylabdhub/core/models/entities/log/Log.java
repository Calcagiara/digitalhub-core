package it.smartcommunitylabdhub.core.models.entities.log;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.base.abstracts.AbstractExtractorProperties;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.log.metadata.LogMetadata;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Log extends AbstractExtractorProperties implements BaseEntity {

    @ValidateField(allowNull = true, fieldType = "uuid", message = "Invalid UUID4 string")
    private String id;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> body = new HashMap<>();

    @Builder.Default
    @JsonIgnore
    private Map<String, Object> extra = new HashMap<>();

    @Builder.Default
    private Map<String, Object> status = new HashMap<>();

    @Builder.Default
    private LogMetadata metadata = new LogMetadata();

    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return this.extra;
    }

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        if (value != null) {
            extra.put(key, value);
        }
    }
}
