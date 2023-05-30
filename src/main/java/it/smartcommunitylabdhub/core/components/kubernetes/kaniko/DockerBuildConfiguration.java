package it.smartcommunitylabdhub.core.components.kubernetes.kaniko;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DockerBuildConfiguration {
    private String dockerTemplatePath;
    private String dockerTargetPath;
    private String baseImage;
    private String entrypointCommand;

    @Builder.Default
    private List<String> additionalCommands = new ArrayList<>();

    public DockerBuildConfiguration addCommand(String value) {
        additionalCommands.add(value);
        return this;
    }
}
