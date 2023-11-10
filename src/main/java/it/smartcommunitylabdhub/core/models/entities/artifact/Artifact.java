package it.smartcommunitylabdhub.core.models.entities.artifact;

import it.smartcommunitylabdhub.core.components.fsm.enums.ArtifactState;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "artifacts")
public class Artifact implements BaseEntity {
    @Id
    @Column(unique = true)
    private String id;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String project;

    @Column(nullable = false)
    private String name;

    @Lob
    private byte[] metadata;


    @Lob
    private byte[] spec;

    @Lob
    private byte[] extra;

    @CreationTimestamp
    @Column(updatable = false)
    private Date created;

    @UpdateTimestamp
    private Date updated;

    private Boolean embedded;

    @Enumerated(EnumType.STRING)
    private ArtifactState state;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}
