package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.function.metadata.FunctionMetadata;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class FunctionDTOBuilder {

    @Autowired
    MetadataConverter<FunctionMetadata> metadataConverter;

    public Function build(
            FunctionEntity function,
            boolean embeddable) {

        return EntityFactory.create(Function::new, function, builder -> builder
                .with(dto -> dto.setId(function.getId()))
                .with(dto -> dto.setKind(function.getKind()))
                .with(dto -> dto.setProject(function.getProject()))
                .with(dto -> dto.setName(function.getName()))
                .with(dto -> {
                    // Set Metadata for function
                    FunctionMetadata functionMetadata =
                            Optional.ofNullable(metadataConverter.reverseByClass(
                                    function.getMetadata(),
                                    FunctionMetadata.class)
                            ).orElseGet(FunctionMetadata::new);

                    functionMetadata.setVersion(function.getId());
                    functionMetadata.setProject(function.getProject());
                    functionMetadata.setName(function.getName());
                    functionMetadata.setEmbedded(function.getEmbedded());
                    functionMetadata.setCreated(function.getCreated());
                    functionMetadata.setUpdated(function.getUpdated());
                    dto.setMetadata(functionMetadata);
                })
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(function.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto
                                .setSpec(ConversionUtils.reverse(
                                        function.getSpec(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(function.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto.setExtra(
                                ConversionUtils.reverse(
                                        function.getExtra(),
                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(function.getEmbedded())
                        .filter(embedded -> !condition || embedded)
                        .ifPresent(embedded -> dto.setStatus(
                                MapUtils.mergeMultipleMaps(
                                        ConversionUtils.reverse(
                                                function.getStatus(),
                                                "cbor"),
                                        Map.of("state",
                                                function.getState())
                                ))
                        )
                )

        );
    }
}
