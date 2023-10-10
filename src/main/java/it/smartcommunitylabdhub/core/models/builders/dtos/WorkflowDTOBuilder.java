package it.smartcommunitylabdhub.core.models.builders.dtos;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowDTO;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.core.models.enums.State;

@Component
public class WorkflowDTOBuilder {

        @Autowired
        MetadataConverter<WorkflowMetadata> metadataConverter;


        public WorkflowDTO build(
                        Workflow workflow,
                        boolean embeddable) {
                return EntityFactory.create(WorkflowDTO::new, workflow, builder -> builder
                                .with(dto -> dto.setId(workflow.getId()))
                                .with(dto -> dto.setKind(workflow.getKind()))
                                .with(dto -> dto.setProject(workflow.getProject()))
                                .with(dto -> dto.setName(workflow.getName()))

                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto.setMetadata(Optional
                                                                .ofNullable(metadataConverter
                                                                                .reverseByClass(workflow
                                                                                                .getMetadata(),
                                                                                                WorkflowMetadata.class))
                                                                .orElseGet(WorkflowMetadata::new))))

                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setSpec(ConversionUtils.reverse(
                                                                                workflow.getSpec(),

                                                                                "cbor"))))
                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setExtra(ConversionUtils.reverse(
                                                                                workflow.getExtra(),

                                                                                "cbor"))))
                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setCreated(workflow.getCreated())))
                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setUpdated(workflow.getUpdated())))
                                .withIfElse(embeddable, (dto, condition) -> Optional
                                                .ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setEmbedded(workflow
                                                                                .getEmbedded())))
                                .withIfElse(embeddable, (dto, condition) ->

                                Optional.ofNullable(workflow.getEmbedded())
                                                .filter(embedded -> !condition
                                                                || (condition && embedded))
                                                .ifPresent(embedded -> dto
                                                                .setState(workflow
                                                                                .getState() == null
                                                                                                ? State.CREATED.name()
                                                                                                : workflow.getState()
                                                                                                                .name())))

                );
        }
}
