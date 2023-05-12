package it.smartcommunitylabdhub.core.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.smartcommunitylabdhub.core.exception.CoreException;
import it.smartcommunitylabdhub.core.exception.CustomException;
import it.smartcommunitylabdhub.core.models.Function;
import it.smartcommunitylabdhub.core.models.Run;
import it.smartcommunitylabdhub.core.models.converters.CommandFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.dtos.FunctionDTO;
import it.smartcommunitylabdhub.core.models.dtos.RunDTO;
import it.smartcommunitylabdhub.core.repositories.FunctionRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.builders.dtos.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.services.builders.entities.FunctionEntityBuilder;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;

@Service
public class FunctionServiceImpl implements FunctionService {

    private final FunctionRepository functionRepository;
    private final RunRepository runRepository;
    private final CommandFactory commandFactory;

    public FunctionServiceImpl(
            FunctionRepository functionRepository,
            RunRepository runRepository,
            CommandFactory commandFactory) {
        this.functionRepository = functionRepository;
        this.runRepository = runRepository;
        this.commandFactory = commandFactory;

    }

    @Override
    public List<FunctionDTO> getFunctions(Pageable pageable) {
        try {
            Page<Function> functionPage = this.functionRepository.findAll(pageable);
            return functionPage.getContent().stream().map((function) -> {
                return new FunctionDTOBuilder(commandFactory, function).build();
            }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        try {
            // Build a function and store it on db
            final Function function = new FunctionEntityBuilder(commandFactory, functionDTO).build();
            this.functionRepository.save(function);

            // Return function DTO
            return new FunctionDTOBuilder(
                    commandFactory,
                    function).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FunctionDTO getFunction(String uuid) {
        final Function function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    "FunctionNotFound",
                    "The function you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {
            return new FunctionDTOBuilder(
                    commandFactory,
                    function).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public FunctionDTO updateFunction(FunctionDTO functionDTO, String uuid) {
        final Function function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    "FunctionNotFound",
                    "The function you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {

            FunctionEntityBuilder functionBuilder = new FunctionEntityBuilder(commandFactory, functionDTO);

            final Function functionUpdated = functionBuilder.update(function);
            this.functionRepository.save(functionUpdated);

            return new FunctionDTOBuilder(
                    commandFactory,
                    functionUpdated).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteFunction(String uuid) {
        try {
            this.functionRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete function",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<RunDTO> getFunctionRuns(String uuid) {
        final Function function = functionRepository.findById(uuid).orElse(null);
        if (function == null) {
            throw new CoreException(
                    "FunctionNotFound",
                    "The function you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {
            List<Run> runs = this.runRepository.findByName(function.getName());
            return (List<RunDTO>) ConversionUtils.reverseIterable(runs, commandFactory, "run", RunDTO.class);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
