package it.smartcommunitylabdhub.core.models.converters.types.mlrun;

import it.smartcommunitylabdhub.core.annotations.common.ConverterType;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.converters.interfaces.Converter;
import it.smartcommunitylabdhub.core.models.entities.function.Function;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ConverterType(type = "mlrunFunction")
public class FunctionToMlrunConverter implements Converter<Function, Map<String, Object>> {

    @Override
    public Map<String, Object> convert(Function functionDTO) throws CustomException {

        return Stream.of(
                        new AbstractMap.SimpleEntry<>("metadata",
                                Stream.of(
                                                new AbstractMap.SimpleEntry<>(
                                                        "name",
                                                        functionDTO.getName()),
                                                new AbstractMap.SimpleEntry<>(
                                                        "project",
                                                        functionDTO.getProject()),
                                                new AbstractMap.SimpleEntry<>("tag",
                                                        Optional.ofNullable(
                                                                        functionDTO
                                                                                .getExtra()
                                                                                .get("tag"))
                                                                .orElse("")),
                                                new AbstractMap.SimpleEntry<>(
                                                        "categories",
                                                        Optional.ofNullable(
                                                                        functionDTO
                                                                                .getExtra()
                                                                                .get("categories"))
                                                                .orElse(new ArrayList<>())))
                                        .filter(entry -> entry
                                                .getValue() != null) // exclude
                                        // null
                                        // values
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue))),
                        new AbstractMap.SimpleEntry<>("kind", functionDTO.getKind()),
                        new AbstractMap.SimpleEntry<>("spec", functionDTO.getSpec()))
                .filter(entry -> entry.getValue() != null) // exclude null values
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    @Override
    public Function reverseConvert(Map<String, Object> input) throws CustomException {
        throw new UnsupportedOperationException("Unimplemented method 'reverseConvert'");
    }

}
