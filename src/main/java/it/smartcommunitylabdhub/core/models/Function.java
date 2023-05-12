package it.smartcommunitylabdhub.core.models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.models.interfaces.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "functions")
public class Function implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String kind;

    @Column(nullable = false)
    private String project;

    @Column(unique = true, nullable = false)
    private String name;

    private byte[] spec;

    private byte[] extra;

    @CreationTimestamp
    @Column(updatable = false)
    private Date created;

    @UpdateTimestamp
    private Date updated;

    private Boolean embedded;

    @Enumerated(EnumType.STRING)
    private State state;

}