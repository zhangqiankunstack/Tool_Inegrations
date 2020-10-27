package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Entity.WorkflowCanvasEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowCanvasRepository extends JpaRepository<WorkflowCanvasEntity,String> {

    List<WorkflowCanvasEntity> findAllByUserEntityAndDeleted(UserEntity user,boolean deleted);

    //模糊查询
    @Query(value = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2) and (?3 is null or is_visible = ?3) Order By create_time Desc",countQuery = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2) and (?3 is null or is_visible = ?3) Order By create_time Desc",nativeQuery = true)
    List<WorkflowCanvasEntity> findAllByNameAndDeletedAndIsVisible(String workFlowCanvasName, boolean deleted, boolean isVisible);

    @Query(value = "SELECT * from workflow_canvas_entity WHERE (?1 is null or deleted = ?1) and (?2 is null or is_visible = ?2) Order By create_time Desc ",countQuery = "SELECT * from workflow_canvas_entity WHERE (?1 is null or deleted = ?1) and (?2 is null or is_visible = ?2) Order By create_time Desc ",nativeQuery = true)
    List<WorkflowCanvasEntity> findByDeletedAndIsVisible(boolean deleted, boolean isVisible);

    List<WorkflowCanvasEntity> findAllByUserEntity(UserEntity userEntity);

    @Query(value = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2) and (?3 is null or user_entity_id = ?3) Order By create_time Desc",countQuery = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2)  and (?3 is null or user_entity_id = ?3) Order By create_time Desc",nativeQuery = true)
    List<WorkflowCanvasEntity> findAllByNameAndDeletedAndIsVisibleAndUserId(String workFlowCanvasName, boolean deleted, String userId);

//    //模糊查询
//    @Query(value = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2)",countQuery = "SELECT * from workflow_canvas_entity WHERE (?1 is null or name like %?1%) and (?2 is null or deleted = ?2)",nativeQuery = true)
//    List<WorkflowCanvasEntity> findAllByNameAndDeletedAndIsVisible(String workFlowCanvasName,boolean deleted,boolean deleted);
}
