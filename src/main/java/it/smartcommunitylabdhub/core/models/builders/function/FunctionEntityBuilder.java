package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionDTO;
import it.smartcommunitylabdhub.core.models.enums.State;
import org.springframework.stereotype.Component;

@Component
public class FunctionEntityBuilder {

    /**
     * Build a function from a functionDTO and store extra values as a cbor
     *
     * @return
     */
    public Function build(FunctionDTO functionDTO) {
        return EntityFactory.combine(
                ConversionUtils.convert(functionDTO, "function"), functionDTO,
                builder -> builder
                        .with(f -> f.setMetadata(
                                ConversionUtils.convert(functionDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(f -> f.setExtra(
                                ConversionUtils.convert(functionDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(f -> f.setSpec(
                                ConversionUtils.convert(functionDTO
                                                .getSpec(),
                                        "cbor"))));
    }

    /**
     * Update a function if element is not passed it override causing empty field
     *
     * @param function
     * @return
     */
    public Function update(Function function, FunctionDTO functionDTO) {
        return EntityFactory.combine(
                function, functionDTO, builder -> builder
                        .with(f -> f.setKind(functionDTO.getKind()))
                        .with(f -> f.setProject(functionDTO.getProject()))
                        .with(f -> f.setName(functionDTO.getName()))
                        .with(f -> f.setState(functionDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(functionDTO
                                .getState())))
                        .with(f -> f.setMetadata(
                                ConversionUtils.convert(functionDTO
                                                .getMetadata(),
                                        "metadata")))

                        .with(f -> f.setExtra(
                                ConversionUtils.convert(functionDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(f -> f.setSpec(
                                ConversionUtils.convert(functionDTO
                                                .getSpec(),

                                        "cbor")))
                        .with(f -> f.setEmbedded(
                                functionDTO.getEmbedded())));
    }
}
