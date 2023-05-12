package it.smartcommunitylabdhub.core.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.smartcommunitylabdhub.core.exception.CoreException;
import it.smartcommunitylabdhub.core.exception.CustomException;
import it.smartcommunitylabdhub.core.models.Workflow;
import it.smartcommunitylabdhub.core.models.Run;
import it.smartcommunitylabdhub.core.models.converters.CommandFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.dtos.WorkflowDTO;
import it.smartcommunitylabdhub.core.models.dtos.RunDTO;
import it.smartcommunitylabdhub.core.repositories.WorkflowRepository;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.builders.dtos.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.services.builders.entities.WorkflowEntityBuilder;
import it.smartcommunitylabdhub.core.services.interfaces.WorkflowService;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final RunRepository runRepository;
    private final CommandFactory commandFactory;

    public WorkflowServiceImpl(
            WorkflowRepository workflowRepository,
            RunRepository runRepository,
            CommandFactory commandFactory) {
        this.workflowRepository = workflowRepository;
        this.runRepository = runRepository;
        this.commandFactory = commandFactory;

    }

    @Override
    public List<WorkflowDTO> getWorkflows(Pageable pageable) {
        try {
            Page<Workflow> workflowPage = this.workflowRepository.findAll(pageable);
            return workflowPage.getContent().stream().map((workflow) -> {
                return new WorkflowDTOBuilder(commandFactory, workflow).build();
            }).collect(Collectors.toList());
        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public WorkflowDTO createWorkflow(WorkflowDTO workflowDTO) {
        try {
            // Build a workflow and store it on db
            final Workflow workflow = new WorkflowEntityBuilder(commandFactory, workflowDTO).build();
            this.workflowRepository.save(workflow);

            // Return workflow DTO
            return new WorkflowDTOBuilder(
                    commandFactory,
                    workflow).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public WorkflowDTO getWorkflow(String uuid) {
        final Workflow workflow = workflowRepository.findById(uuid).orElse(null);
        if (workflow == null) {
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {
            return new WorkflowDTOBuilder(
                    commandFactory,
                    workflow).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public WorkflowDTO updateWorkflow(WorkflowDTO workflowDTO, String uuid) {
        final Workflow workflow = workflowRepository.findById(uuid).orElse(null);
        if (workflow == null) {
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {

            WorkflowEntityBuilder workflowBuilder = new WorkflowEntityBuilder(commandFactory, workflowDTO);

            final Workflow workflowUpdated = workflowBuilder.update(workflow);
            this.workflowRepository.save(workflowUpdated);

            return new WorkflowDTOBuilder(
                    commandFactory,
                    workflowUpdated).build();

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean deleteWorkflow(String uuid) {
        try {
            this.workflowRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException(
                    "InternalServerError",
                    "cannot delete workflow",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<RunDTO> getWorkflowRuns(String uuid) {
        final Workflow workflow = workflowRepository.findById(uuid).orElse(null);
        if (workflow == null) {
            throw new CoreException(
                    "WorkflowNotFound",
                    "The workflow you are searching for does not exist.",
                    HttpStatus.NOT_FOUND);
        }

        try {
            List<Run> runs = this.runRepository.findByName(workflow.getName());
            return (List<RunDTO>) ConversionUtils.reverseIterable(runs, commandFactory, "run", RunDTO.class);

        } catch (CustomException e) {
            throw new CoreException(
                    "InternalServerError",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
