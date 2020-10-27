package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolConsequenceRepository;
import com.rengu.toolintegrations.Repository.ToolDrawingNodeRepository;
import com.rengu.toolintegrations.Repository.ToolRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 11:38
 */
@Service
public class ToolDrawingNodeService {
    private final ToolDrawingNodeRepository toolDrawingNodeRepository;
    private final ToolRepository toolRepository;
    //    private final ToolConsequenceRepository toolConsequenceRepository;
    private final ToolConsequenceFileService toolConsequenceFileService;

    @Autowired
    public ToolDrawingNodeService(ToolDrawingNodeRepository toolDrawingNodeRepository, ToolRepository toolRepository, ToolConsequenceFileService toolConsequenceFileService) {
        this.toolDrawingNodeRepository = toolDrawingNodeRepository;
        this.toolRepository = toolRepository;
//        this.toolConsequenceRepository = toolConsequenceRepository;
        this.toolConsequenceFileService = toolConsequenceFileService;
    }

    //通过画布清除绘图节点
    public List<ToolDrawingNodeEntity> cleanToolDrawingNodeByWorkflowCanvasEntity(WorkflowCanvasEntity workflowCanvasEntity) {
        List<ToolDrawingNodeEntity> toolDrawingNodeEntityList = getToolDrawingNodeByWorkFlowCanvasId(workflowCanvasEntity);
        for (ToolDrawingNodeEntity toolDrawingNodeEntity : toolDrawingNodeEntityList) {
            toolDrawingNodeRepository.delete(toolDrawingNodeEntity);
        }
        return toolDrawingNodeEntityList;
    }

    //保存
    public ToolDrawingNodeEntity saveToolDrawingNode(ToolDrawingNodeEntity toolDrawingNodeEntity) {
        return toolDrawingNodeRepository.save(toolDrawingNodeEntity);
    }

    //根據id查詢繪圖節點
    public ToolDrawingNodeEntity findDrawingNodeById(String toolDrawingNodeId) {
        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
        return toolDrawingNodeRepository.findById(toolDrawingNodeEntity.getId()).get();
    }

    //根据画布和成果id删除工具画布节点
    public ToolDrawingNodeEntity deletedToolDrawingNodeByWorkFlowIdAndToolConseId(String toolDrawingNodeId, ToolConsequenceEntity toolConsequenceEntity) throws IOException {
        //ToolConsequenceEntity toolConsequenceEntity = toolConsequenceService.getToolConsequenceById(tooConsequenceId);
        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
        toolDrawingNodeEntity.setToolEntity(null);
        //通过id删除工具成果
        toolConsequenceFileService.deletedToolConsequenceFileByToolConsequence(toolConsequenceEntity);
        return toolDrawingNodeEntity;
    }

    //查询全部节点
    public List<ToolDrawingNodeEntity> getToolDrawingNodeByWorkFlowCanvasId(WorkflowCanvasEntity workFlowCanvasEntity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return toolDrawingNodeRepository.findAllByWorkflowCanvasEntity(workFlowCanvasEntity, sort);
    }

    //根据工具删除工具绘图节点
    public List<ToolDrawingNodeEntity> cleanToolWorkFlowNodeByToolEntity(ToolEntity toolEntity) {
        List<ToolDrawingNodeEntity> toolDrawingNodeEntityList = getToolDrawingNodeByToolEntity(toolEntity);
        for (ToolDrawingNodeEntity toolDrawingNode : toolDrawingNodeEntityList) {
            toolDrawingNodeRepository.delete(toolDrawingNode);
        }
        return toolDrawingNodeEntityList;
    }

    //通过工具获取工具绘图节点
    public List<ToolDrawingNodeEntity> getToolDrawingNodeByToolEntity(ToolEntity toolEntity) {
        return toolDrawingNodeRepository.findAllByToolEntity(toolEntity);
    }

    //根据id绑定工具
    public ToolDrawingNodeEntity bindToolById(String toolDrawingNodeId, ToolEntity toolEntity) {
        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
        //先判断工具是否被绑定过
        boolean ifBind = toolEntity.isIfBind();
        System.out.println("获取的值为：" + ifBind);
        if (!ifBind == false) {
            throw new RuntimeException(ApplicationMessages.The_TOOL_HAS_BEEN_BOUND + toolEntity);
        }
        toolEntity.setIfBind(true);
        toolRepository.save(toolEntity);
        toolDrawingNodeEntity.setToolEntity(toolEntity);
        return toolDrawingNodeRepository.save(toolDrawingNodeEntity);
    }

//    //根据id绑定工具成果
//    public ToolDrawingNodeEntity bindToolConsequenceById(String toolDrawingNodeId, String toolConsequenceId) {
//        ToolConsequenceEntity toolConsequenceEntity = toolConsequenceService.getToolConsequenceById(toolConsequenceId);
//        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
////        //先判断工具是否被绑定过
////        boolean ifBind = toolConsequenceEntity.isIfBind();
////        System.out.println("获取的值为：" + ifBind);
////        if (!ifBind == false) {
////            throw new RuntimeException(ApplicationMessages.The_TOOL_CONSEQUENCE_HAS_BEEN_BOUND + toolConsequenceEntity);
////        }
////        toolConsequenceEntity.setIfBind(true);
//        toolConsequenceRepository.save(toolConsequenceEntity);
//        toolDrawingNodeEntity.setToolConsequenceEntity(toolConsequenceEntity);
//        return toolDrawingNodeRepository.save(toolDrawingNodeEntity);
//    }

//    //解绑工具
//    public ToolDrawingNodeEntity unbindToolById(String toolDrawingNodeId, ToolEntity toolEntity) {
//        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
//        toolEntity.setIfBind(false);
//        toolRepository.save(toolEntity);
//        toolDrawingNodeEntity.setToolEntity(null);
//        return toolDrawingNodeRepository.save(toolDrawingNodeEntity);
//    }

//    //解绑工具成果
//    public ToolDrawingNodeEntity unbindToolConsequenceById(String toolDrawingNodeId, String toolConsequenceId) {
//        ToolConsequenceEntity toolConsequenceEntity = toolConsequenceService.getToolConsequenceById(toolConsequenceId);
//        ToolDrawingNodeEntity toolDrawingNodeEntity = getToolDrawingNodeById(toolDrawingNodeId);
////        toolConsequenceEntity.setIfBind(false);
//        toolConsequenceRepository.save(toolConsequenceEntity);
//        toolDrawingNodeEntity.setToolConsequenceEntity(null);
//        return toolDrawingNodeRepository.save(toolDrawingNodeEntity);
//    }


    public ToolDrawingNodeEntity getToolDrawingNodeById(String toolDrawingNodeId) {
        if (!hasToolDrawingNodeById(toolDrawingNodeId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_DRAWING_NODE_ID_NOT_FOUND + toolDrawingNodeId);
        }
        return toolDrawingNodeRepository.findById(toolDrawingNodeId).get();
    }

    public boolean hasToolDrawingNodeById(String toolDrawingNodeId) {
        if (StringUtils.isEmpty(toolDrawingNodeId)) {
            return false;
        }
        return toolDrawingNodeRepository.existsById(toolDrawingNodeId);
    }

    //通过工具成果
    public List<ToolDrawingNodeEntity> deletedToolDrawingNodeByToolConse(ToolConsequenceEntity toolConsequenceEntity) {
        List<ToolDrawingNodeEntity> toolDrawingNodeEntityList = getToolDrawingNodeByToolConsequenceEntity(toolConsequenceEntity);
        for (ToolDrawingNodeEntity toolDrawingNodeEntity : toolDrawingNodeEntityList) {
            toolDrawingNodeRepository.delete(toolDrawingNodeEntity);
        }
        return toolDrawingNodeEntityList;
    }

    public List<ToolDrawingNodeEntity> getToolDrawingNodeByToolConsequenceEntity(ToolConsequenceEntity toolConsequenceEntity) {
        return toolDrawingNodeRepository.findAllByToolConsequenceEntity(toolConsequenceEntity);
    }
}
