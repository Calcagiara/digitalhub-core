package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.RuntimeFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.kinds.factory.builders.KindBuilderFactory;
import it.smartcommunitylabdhub.core.components.kinds.factory.publishers.KindPublisherFactory;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.TaskUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.run.RunDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.run.RunEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunDTO;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.core.repositories.RunRepository;
import it.smartcommunitylabdhub.core.services.interfaces.FunctionService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import it.smartcommunitylabdhub.core.services.interfaces.TaskService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class RunSerivceImpl implements RunService {

    @Autowired
    RunDTOBuilder runDTOBuilder;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    FunctionService functionService;

    @Autowired
    RuntimeFactory runtimeFactory;

    @Autowired
    KindBuilderFactory runBuilderFactory;

    @Autowired
    KindPublisherFactory runPublisherFactory;

    @Autowired
    RunEntityBuilder runEntityBuilder;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Override
    public List<RunDTO> getRuns(Pageable pageable) {
        try {
            Page<Run> runPage = this.runRepository.findAll(pageable);
            return runPage.getContent().stream().map(run -> runDTOBuilder.build(run))
                    .collect(Collectors.toList());

        } catch (CustomException e) {
            throw new CoreException(ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RunDTO getRun(String uuid) {
        return runRepository.findById(uuid).map(run -> runDTOBuilder.build(run))
                .orElseThrow(() -> new CoreException(
                        ErrorList.RUN_NOT_FOUND.getValue(),
                        ErrorList.RUN_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public boolean deleteRun(String uuid) {
        try {
            this.runRepository.deleteById(uuid);
            return true;
        } catch (Exception e) {
            throw new CoreException(ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete artifact",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RunDTO save(RunDTO runDTO) {

        return Optional.of(this.runRepository.save(runEntityBuilder.build(runDTO)))
                .map(run -> runDTOBuilder.build(run))
                .orElseThrow(() -> new CoreException(
                        "RunSaveError",
                        "Problem while saving the run.",
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public RunDTO updateRun(RunDTO runDTO, String uuid) {

        if (!runDTO.getId().equals(uuid)) {
            throw new CoreException(
                    ErrorList.RUN_NOT_MATCH.getValue(),
                    ErrorList.RUN_NOT_MATCH.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        final Run run = runRepository.findById(uuid).orElse(null);
        if (run == null) {
            throw new CoreException(
                    ErrorList.RUN_NOT_FOUND.getValue(),
                    ErrorList.RUN_NOT_FOUND.getReason(),
                    HttpStatus.NOT_FOUND);
        }

        try {
            final Run runUpdated = runEntityBuilder.update(run, runDTO);
            this.runRepository.save(runUpdated);

            return runDTOBuilder.build(runUpdated);

        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public RunDTO createRun(RunDTO runDTO) {

        // Retrieve Run base spec
        RunBaseSpec runBaseSpec = (RunBaseSpec) specRegistry.createSpec(
                runDTO.getKind(), runDTO.getSpec()
        );

        // Check if run already exist with the passed uuid
        if (runRepository.existsById(Optional.ofNullable(runDTO.getId()).orElse(""))) {
            throw new CoreException(
                    ErrorList.DUPLICATE_RUN.getValue(),
                    ErrorList.DUPLICATE_RUN.getReason(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Retrieve task
        return Optional.ofNullable(this.taskService.getTask(runBaseSpec.getTaskId()))
                .map(taskDTO -> {
                    TaskBaseSpec taskSpec = (TaskBaseSpec) specRegistry.createSpec(
                            taskDTO.getKind(), taskDTO.getSpec());
                    // Parse task to get accessor
                    TaskAccessor taskAccessor =
                            TaskUtils.parseTask(taskSpec.getFunction());

                    return Optional
                            .ofNullable(functionService.getFunction(
                                    taskAccessor.getVersion()))
                            .map(functionDTO -> {

                                FunctionBaseSpec funcSpec = (FunctionBaseSpec) specRegistry.createSpec(
                                        functionDTO.getKind(), functionDTO.getSpec()
                                );


                                // Update spec object for run
                                runDTO.setProject(taskAccessor.getProject());


                                // Check weather the run has local
                                // set to True in that case return
                                // immediately the run without
                                // invoke the execution.
                                Supplier<RunDTO> result =
                                        () -> Optional
                                                .of(runBaseSpec.isLocalExecution()) // return
                                                // if true
                                                .filter(value -> value
                                                        .equals(true))

                                                .map(value -> {
                                                    // Save the run and return immediately
                                                    Run run = runRepository.save(
                                                            runEntityBuilder.build(runDTO));
                                                    return runDTOBuilder.build(run);
                                                })
                                                // exec run and return run dto
                                                .orElseGet(() -> {

                                                    // Retrieve Runtime and build run
                                                    Runtime runtime = runtimeFactory
                                                            .getRuntime(taskAccessor.getRuntime());


                                                    // Build RunSpec using Runtime
                                                    RunRunSpec runSpecBuilt = (RunRunSpec) runtime.build(
                                                            funcSpec,
                                                            taskSpec,
                                                            runBaseSpec,
                                                            taskDTO.getKind());

                                                    // Update run spec
                                                    runDTO.setSpec(runSpecBuilt.toMap());

                                                    // Update run state to BUILT
                                                    runDTO.setState(RunState.BUILT.toString());

                                                    // Save Run
                                                    Run run = runRepository.save(
                                                            runEntityBuilder.build(runDTO));

                                                    // Create Runnable
                                                    Runnable runnable =
                                                            runtime.run(
                                                                    runDTOBuilder.build(run)
                                                            );

                                                    // Dispatch Runnable
                                                    eventPublisher.publishEvent(
                                                            runnable);

                                                    // Return saved run
                                                    return runDTOBuilder.build(run);
                                                });

                                return result.get();
                            }).orElseThrow(
                                    () -> new CoreException(
                                            ErrorList.FUNCTION_NOT_FOUND
                                                    .getValue(),
                                            ErrorList.FUNCTION_NOT_FOUND
                                                    .getReason(),
                                            HttpStatus.NOT_FOUND));


                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.RUN_NOT_FOUND.getValue(),
                        ErrorList.RUN_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND));

    }

}
