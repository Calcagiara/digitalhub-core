package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.WorkflowFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.specs.WorkflowBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkflowEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a workflow from a workflowDTO and store extra values as a cbor
     *
     * @return Workflow
     */
    public WorkflowEntity build(Workflow workflowDTO) {

        // Validate spec
        specRegistry.createSpec(workflowDTO.getKind(), EntityName.WORKFLOW, Map.of());

        // Retrieve Field accessor
        WorkflowFieldAccessor<?> workflowFieldAccessor =
                accessorRegistry.createAccessor(
                        workflowDTO.getKind(),
                        EntityName.WORKFLOW,
                        JacksonMapper.objectMapper.convertValue(workflowDTO,
                                JacksonMapper.typeRef));


        // Retrieve Spec
        WorkflowBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(workflowDTO.getSpec(), WorkflowBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(workflowDTO, "workflow"), workflowDTO, builder -> builder
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                workflowDTO.getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                workflowDTO.getExtra(), "cbor")))
                        .with(a -> a.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))

                        // Store status if not present
                        .withIfElse(workflowFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )

                        // Metadata Extraction
                        .withIfElse(workflowDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(workflowDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
                        .withIf(workflowDTO.getMetadata().getCreated() != null, (a) ->
                                a.setCreated(workflowDTO.getMetadata().getCreated()))
                        .withIf(workflowDTO.getMetadata().getUpdated() != null, (a) ->
                                a.setUpdated(workflowDTO.getMetadata().getUpdated()))
        );

    }

    /**
     * Update a workflow if element is not passed it override causing empty field
     *
     * @param workflow Workflow
     * @return WorkflowEntity
     */
    public WorkflowEntity update(WorkflowEntity workflow, Workflow workflowDTO) {

        // Validate Spec
        specRegistry.createSpec(workflowDTO.getKind(), EntityName.WORKFLOW, Map.of());

        // Retrieve Field accessor
        WorkflowFieldAccessor<?> workflowFieldAccessor =
                accessorRegistry.createAccessor(
                        workflowDTO.getKind(),
                        EntityName.WORKFLOW,
                        JacksonMapper.objectMapper.convertValue(workflowDTO,
                                JacksonMapper.typeRef));


        return EntityFactory.combine(
                workflow, workflowDTO, builder -> builder
                        .withIfElse(workflowFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(workflowFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(a -> a.setMetadata(ConversionUtils.convert(workflowDTO
                                .getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(workflowDTO
                                .getExtra(), "cbor")))

                        // Metadata Extraction
                        .withIfElse(workflowDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(workflowDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
        );
    }
}
