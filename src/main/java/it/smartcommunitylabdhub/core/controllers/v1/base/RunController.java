package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.core.annotations.common.ApiVersion;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.entities.log.Log;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.services.interfaces.LogService;
import it.smartcommunitylabdhub.core.services.interfaces.RunService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/runs")
@ApiVersion("v1")
@Tag(name = "Run base API", description = "Endpoints related to runs management out of the Context")
public class RunController {

    @Autowired
    RunService runService;

    @Autowired
    LogService logService;

    @Operation(summary = "Get a run", description = "Given an uuid return the related Run")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Run> getRun(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.runService.getRun(uuid));
    }

    @Operation(summary = "Run log list", description = "Return the log list for a specific run")
    @GetMapping(path = "/{uuid}/log", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Log>> getRunLog(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid,
            Pageable pageable) {
        return ResponseEntity.ok(this.logService.getLogsByRunUuid(uuid, pageable));
    }

    @Operation(summary = "Run list", description = "Return a list of all runs")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<Run>> getRuns(
            @RequestParam Map<String, String> filter,
            Pageable pageable) {
        return ResponseEntity.ok(this.runService.getRuns(filter, pageable));
    }

    @Operation(summary = "Create and execute a run",
            description = "Create a run and then execute it")
    @PostMapping(path = "", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Run> createRun(@Valid @RequestBody Run inputRunDTO) {
        return ResponseEntity.ok(this.runService.createRun(inputRunDTO));
    }


    @Operation(summary = "Update specific run", description = "Update and return the update run")
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml"}, produces = "application/json; charset=UTF-8")
    public ResponseEntity<Run> updateRun(@Valid @RequestBody Run runDTO,
                                         @ValidateField @PathVariable String uuid) {
        return ResponseEntity.ok(this.runService.updateRun(runDTO, uuid));
    }


    @Operation(summary = "Delete a run", description = "Delete a specific run")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteRun(
            @ValidateField @PathVariable(name = "uuid", required = true) String uuid,
            @RequestParam(name = "cascade", defaultValue = "false") Boolean cascade) {
        return ResponseEntity.ok(this.runService.deleteRun(uuid, cascade));
    }
}
