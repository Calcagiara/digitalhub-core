package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunDTO;
import it.smartcommunitylabdhub.core.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.core.models.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RunDTOBuilder {

    @Autowired
    MetadataConverter<RunMetadata> metadataConverter;

    public RunDTO build(Run run) {
        return EntityFactory.create(RunDTO::new, run, builder -> builder
                .with(dto -> dto.setId(run.getId()))
                .with(dto -> dto.setKind(run.getKind()))
                .with(dto -> dto.setTaskId(run.getTaskId()))
                .with(dto -> dto.setProject(run.getProject()))
                .with(dto -> dto.setTask(run.getTask()))
                .with(dto -> dto.setMetadata(Optional
                        .ofNullable(metadataConverter.reverseByClass(
                                run.getMetadata(),
                                RunMetadata.class))
                        .orElseGet(RunMetadata::new)

                ))
                .with(dto -> dto.setSpec(
                        ConversionUtils.reverse(run.getSpec(), "cbor")))
                .with(dto -> dto.setExtra(
                        ConversionUtils.reverse(run.getExtra(), "cbor")))
                .with(dto -> dto.setCreated(run.getCreated()))
                .with(dto -> dto.setUpdated(run.getUpdated()))
                .with(dto -> dto.setState(run.getState() == null
                        ? State.CREATED.name()
                        : run.getState()
                        .name()))

        );
    }
}
