package it.smartcommunitylabdhub.core.models.entities.workflow;

import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.models.enums.State;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "workflows")
@EntityListeners(AuditingEntityListener.class)
public class WorkflowEntity implements BaseEntity {

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

    @Lob
    private byte[] status;

    @CreatedDate
    @Column(updatable = false)
    private Date created;

    @LastModifiedDate
    private Date updated;

    private Boolean embedded;

    @Enumerated(EnumType.STRING)
    private State state;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

}
