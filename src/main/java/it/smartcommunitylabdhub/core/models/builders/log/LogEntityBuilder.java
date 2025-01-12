package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.LogFieldAccessor;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class LogEntityBuilder {


    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a Log from a LogDTO and store extra values as a cbor
     *
     * @return LogEntity
     */
    public LogEntity build(Log logDTO) {

        // Retrieve field accessor
        LogFieldAccessor<?> logFieldAccessor =
                accessorRegistry.createAccessor(
                        "log",
                        EntityName.LOG,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(logDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                ConversionUtils.convert(logDTO, "log"), logDTO,
                builder -> builder
                        // check id
                        .withIfElse(logDTO.getId() != null &&
                                        logDTO.getMetadata().getVersion() != null,
                                (l) -> {
                                    if (logDTO.getId()
                                            .equals(logDTO.getMetadata().getVersion())) {
                                        l.setId(logDTO.getMetadata().getVersion());
                                    } else {
                                        throw new CoreException(
                                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                                "Trying to store item with which has different signature <id != version>",
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    }
                                },
                                (l) -> {
                                    if (logDTO.getId() == null &&
                                            logDTO.getMetadata().getVersion() != null) {
                                        l.setId(logDTO.getMetadata().getVersion());
                                    } else {
                                        l.setId(logDTO.getId());
                                    }
                                })
                        .with(l -> l.setMetadata(ConversionUtils.convert(
                                logDTO.getMetadata(), "metadata")))
                        .with(l -> l.setExtra(ConversionUtils.convert(
                                logDTO.getExtra(), "cbor")))
                        .with(l -> l.setBody(ConversionUtils.convert(
                                logDTO.getBody(), "cbor")))
                        .with(l -> l.setStatus(ConversionUtils.convert(
                                logDTO.getStatus(), "cbor")))

                        // Store status if not present
                        .withIfElse(logFieldAccessor.getState().equals(State.NONE.name()),
                                (l, condition) -> {
                                    if (condition) {
                                        l.setState(State.CREATED);
                                    } else {
                                        l.setState(State.valueOf(logFieldAccessor.getState()));
                                    }
                                }
                        )
                        .withIf(logDTO.getMetadata().getRun() != null, (l) ->
                                l.setRun(logDTO.getMetadata().getRun()))
                        .withIf(logDTO.getMetadata().getProject() != null, (l) ->
                                l.setProject(logDTO.getMetadata().getProject()))
                        .withIf(logDTO.getMetadata().getCreated() != null, (l) ->
                                l.setCreated(logDTO.getMetadata().getCreated()))
                        .withIf(logDTO.getMetadata().getUpdated() != null, (l) ->
                                l.setUpdated(logDTO.getMetadata().getUpdated()))
        );
    }

    /**
     * Update a Log if element is not passed it override causing empty field
     *
     * @param log Log
     * @return LogEntity
     */
    public LogEntity update(LogEntity log, Log logDTO) {

        LogFieldAccessor<?> logFieldAccessor =
                accessorRegistry.createAccessor(
                        "log",
                        EntityName.LOG,
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(logDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                log, logDTO, builder -> builder
                        .with(l -> l.setMetadata(ConversionUtils.convert(
                                logDTO.getMetadata(), "metadata")))
                        .with(l -> l.setExtra(ConversionUtils.convert(
                                logDTO.getExtra(), "cbor")))
                        .with(l -> l.setStatus(ConversionUtils.convert(
                                logDTO.getStatus(), "cbor")))
                        .with(l -> l.setBody(ConversionUtils.convert(
                                logDTO.getBody(), "cbor")))

                        // Store status if not present
                        .withIfElse(logFieldAccessor.getState().equals(State.NONE.name()),
                                (l, condition) -> {
                                    if (condition) {
                                        l.setState(State.CREATED);
                                    } else {
                                        l.setState(State.valueOf(logFieldAccessor.getState()));
                                    }
                                }
                        )
                        .withIf(logDTO.getMetadata().getRun() != null, (l) ->
                                l.setRun(logDTO.getMetadata().getRun()))
                        .withIf(logDTO.getMetadata().getProject() != null, (l) ->
                                l.setProject(logDTO.getMetadata().getProject()))
                        .withIf(logDTO.getMetadata().getCreated() != null, (l) ->
                                l.setCreated(logDTO.getMetadata().getCreated()))
                        .withIf(logDTO.getMetadata().getUpdated() != null, (l) ->
                                l.setUpdated(logDTO.getMetadata().getUpdated()))

        );
    }
}
