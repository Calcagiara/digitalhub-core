package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RunRepository extends JpaRepository<RunEntity, String>, JpaSpecificationExecutor<RunEntity> {

    List<RunEntity> findByProject(String uuid);

    List<RunEntity> findByTask(String task);

    @Modifying
    @Query("DELETE FROM RunEntity r WHERE r.project = :project ")
    void deleteByProjectName(@Param("project") String project);

    @Modifying
    @Query("DELETE FROM RunEntity r WHERE r.taskId = :taskId ")
    void deleteByTaskId(String taskId);


    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////

    Page<RunEntity> findAllByProjectOrderByCreatedDesc(String project, Pageable pageable);


    Optional<RunEntity> findByProjectAndId(@Param("project") String project,
                                           @Param("id") String id);


    boolean existsByProjectAndId(String project, String id);

    @Modifying
    @Query("DELETE FROM RunEntity a WHERE a.project = :project AND a.id = :id")
    void deleteByProjectAndId(@Param("project") String project,
                              @Param("id") String id);

}
