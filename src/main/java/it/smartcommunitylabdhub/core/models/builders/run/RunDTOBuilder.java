package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.metadata.RunMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RunDTOBuilder {

    @Autowired
    MetadataConverter<RunMetadata> metadataConverter;

    public Run build(RunEntity run) {

        return EntityFactory.create(Run::new, run, builder -> builder
                .with(dto -> dto.setId(run.getId()))
                .with(dto -> dto.setKind(run.getKind()))
                .with(dto -> dto.setProject(run.getProject()))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(metadataConverter.reverseByClass(
                                run.getMetadata(),
                                RunMetadata.class))
                        .orElseGet(RunMetadata::new)

                ))
                .with(dto -> dto.setSpec(ConversionUtils.reverse(
                        run.getSpec(), "cbor")))
                .with(dto -> dto.setExtra(ConversionUtils.reverse(
                        run.getExtra(), "cbor")))
                .with(dto -> dto.setCreated(run.getCreated()))
                .with(dto -> dto.setUpdated(run.getUpdated()))
                .with(dto -> dto.setStatus(
                        ConversionUtils.reverse(
                                run.getStatus(), "cbor")
                ))

        );
    }
}
