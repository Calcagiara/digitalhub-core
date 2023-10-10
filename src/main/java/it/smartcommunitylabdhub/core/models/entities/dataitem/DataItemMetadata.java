package it.smartcommunitylabdhub.core.models.entities.dataitem;

import it.smartcommunitylabdhub.core.models.base.Metadata;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataItemMetadata extends Metadata {
	@NotNull
	String name;

	@NotNull
	String version;

	@NotNull
	String description;

	@NotNull
	String embedded;
}
