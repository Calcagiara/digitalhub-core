package it.smartcommunitylabdhub.core.controllers.v1;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.dtos.ArtifactDTO;
import it.smartcommunitylabdhub.core.services.interfaces.ArtifactService;

@RestController
@RequestMapping("/artifacts")
@ApiVersion("v1")
public class ArtifactController {

    private final ArtifactService artifactService;

    public ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<ArtifactDTO>> getArtifacts(Pageable pageable) {
        return ResponseEntity.ok(this.artifactService.getArtifacts(pageable));
    }

    @PostMapping(value = "", consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" })
    public ResponseEntity<ArtifactDTO> createArtifact(@RequestBody ArtifactDTO artifactDTO) {
        return ResponseEntity.ok(this.artifactService.createArtifact(artifactDTO));
    }

    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<ArtifactDTO> getArtifact(@PathVariable(name = "uuid", required = true) String uuid) {
        return ResponseEntity.ok(this.artifactService.getArtifact(uuid));
    }

    @PutMapping(path = "/{uuid}", consumes = { MediaType.APPLICATION_JSON_VALUE,
            "application/x-yaml" }, produces = "application/json")
    public ResponseEntity<ArtifactDTO> updateArtifact(@RequestBody ArtifactDTO artifactDTO, @PathVariable String uuid) {
        return ResponseEntity.ok(this.artifactService.updateArtifact(artifactDTO, uuid));
    }

    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteArtifact(@PathVariable String uuid) {
        return ResponseEntity.ok(this.artifactService.deleteArtifact(uuid));
    }

}
