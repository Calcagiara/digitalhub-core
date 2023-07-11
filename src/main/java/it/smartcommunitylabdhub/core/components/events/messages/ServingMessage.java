package it.smartcommunitylabdhub.core.components.events.messages;

import it.smartcommunitylabdhub.core.components.events.messages.interfaces.Message;
import it.smartcommunitylabdhub.core.models.dtos.RunDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServingMessage implements Message {
    private RunDTO runDTO;
}