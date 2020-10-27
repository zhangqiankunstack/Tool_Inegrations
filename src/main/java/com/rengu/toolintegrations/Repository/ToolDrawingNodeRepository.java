package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolConsequenceEntity;
import com.rengu.toolintegrations.Entity.ToolDrawingNodeEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.WorkflowCanvasEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolDrawingNodeRepository extends JpaRepository<ToolDrawingNodeEntity,String> {

    List<ToolDrawingNodeEntity> findAllByWorkflowCanvasEntity(WorkflowCanvasEntity workflowCanvasEntity, Sort sort);

    List<ToolDrawingNodeEntity> findAllByToolEntity(ToolEntity toolEntity);

    List<ToolDrawingNodeEntity> findAllByToolConsequenceEntity(ToolConsequenceEntity toolConsequenceEntity);

    //List<ToolDrawingNodeEntity> findAllByWorkflowCanvasEntity(WorkflowCanvasEntity workflowCanvasEntity);

//    List<ToolDrawingNodeEntity> findAllByUserEntity(UserEntity userEntity);

//    List<ToolDrawingNodeEntity> findByToolWorkToolWorkFlowNodeEntity(ToolWorkFlowNodeEntity toolWorkFlowNodeEntity);
}
