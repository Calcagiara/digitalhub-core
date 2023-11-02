package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemDTO;
import it.smartcommunitylabdhub.core.models.entities.dataitem.metadata.DataItemBaseMetadata;
import it.smartcommunitylabdhub.core.models.enums.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataItemDTOBuilder {

    @Autowired
    MetadataConverter<DataItemBaseMetadata> metadataConverter;

    public DataItemDTO build(DataItem dataItem, boolean embeddable) {


        return EntityFactory.create(DataItemDTO::new, dataItem, builder -> builder
                .with(dto -> dto.setId(dataItem.getId()))
                .with(dto -> dto.setKind(dataItem.getKind()))
                .with(dto -> dto.setProject(dataItem.getProject()))
                .with(dto -> dto.setName(dataItem.getName()))

                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto.setMetadata(Optional
                                .ofNullable(metadataConverter
                                        .reverseByClass(dataItem
                                                        .getMetadata(),
                                                DataItemBaseMetadata.class))
                                .orElseGet(DataItemBaseMetadata::new))))

                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setSpec(ConversionUtils.reverse(
                                        dataItem.getSpec(), "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setExtra(ConversionUtils.reverse(
                                        dataItem.getExtra(),

                                        "cbor"))))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setCreated(dataItem.getCreated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setUpdated(dataItem.getUpdated())))
                .withIfElse(embeddable, (dto, condition) -> Optional
                        .ofNullable(dataItem.getEmbedded())
                        .filter(embedded -> !condition
                                || (condition && embedded))
                        .ifPresent(embedded -> dto
                                .setEmbedded(dataItem
                                        .getEmbedded())))
                .withIfElse(embeddable, (dto, condition) ->

                        Optional.ofNullable(dataItem.getEmbedded())
                                .filter(embedded -> !condition
                                        || (condition && embedded))
                                .ifPresent(embedded -> dto
                                        .setState(dataItem
                                                .getState() == null
                                                ? State.CREATED.name()
                                                : dataItem.getState()
                                                .name()))

                ));
    }
}
