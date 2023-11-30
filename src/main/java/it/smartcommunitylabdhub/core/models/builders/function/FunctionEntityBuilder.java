package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class FunctionEntityBuilder {


    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build a function from a functionDTO and store extra values as a cbor
     * <p>
     * * Autowired
     * * SpecRegistry<? extends Spec> specRegistry;
     * * specRegistry.createSpec(functionDTO.getKind(), EntityName.FUNCTION, Map.of());
     *
     * @param functionDTO the functionDTO that need to be stored
     * @return Function
     */
    public FunctionEntity build(Function functionDTO) {

        // Retrieve field accessor
        FunctionFieldAccessor<?> functionFieldAccessor =
                accessorRegistry.createAccessor(
                        functionDTO.getKind(),
                        EntityName.FUNCTION,
                        JacksonMapper.objectMapper.convertValue(functionDTO,
                                JacksonMapper.typeRef));

        // Retrieve Spec
        FunctionBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(functionDTO.getSpec(), FunctionBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(functionDTO, "function"), functionDTO,
                builder -> builder
                        .withIfElse(functionFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        functionFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        functionFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(functionFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(f -> f.setMetadata(
                                ConversionUtils.convert(functionDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(f -> f.setExtra(
                                ConversionUtils.convert(functionDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(f -> f.setSpec(
                                ConversionUtils.convert(spec.toMap(),
                                        "cbor"))));
    }

    /**
     * Update a function if element is not passed it override causing empty field
     *
     * @param function the function to update
     * @return Function
     */
    public FunctionEntity update(FunctionEntity function, Function functionDTO) {

        // Retrieve field accessor
        FunctionFieldAccessor<?> functionFieldAccessor =
                accessorRegistry.createAccessor(
                        functionDTO.getKind(),
                        EntityName.FUNCTION,
                        JacksonMapper.objectMapper.convertValue(functionDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                function, functionDTO, builder -> builder
                        .withIfElse(functionFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        functionFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        functionFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(functionFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(f -> f.setMetadata(
                                ConversionUtils.convert(functionDTO
                                                .getMetadata(),
                                        "metadata")))

                        .with(f -> f.setExtra(
                                ConversionUtils.convert(functionDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(f -> f.setEmbedded(
                                functionDTO.getEmbedded())));
    }
}
