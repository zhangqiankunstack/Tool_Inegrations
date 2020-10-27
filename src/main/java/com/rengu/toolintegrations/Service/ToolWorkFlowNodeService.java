//package com.rengu.toolintegrations.Service;
//
//import com.rengu.toolintegrations.Entity.ToolConsequenceEntity;
//import com.rengu.toolintegrations.Entity.ToolDrawingNodeEntity;
//import com.rengu.toolintegrations.Entity.ToolEntity;
//import com.rengu.toolintegrations.Entity.ToolWorkFlowNodeEntity;
//import com.rengu.toolintegrations.Repository.*;
//import com.rengu.toolintegrations.Utils.ApplicationMessages;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//
//*
// * Author: Zhangqiankun
// * Date: 2020/9/1 10:11
//
//
//@Service
//public class ToolWorkFlowNodeService {
//    private final ToolRepository toolRepository;
//    private final ToolConsequenceRepository toolConsequenceRepository;
//    //    private final ToolDrawingNodeService toolDrawingNodeService;
////    private final ToolDrawingNodeRepository toolDrawingNodeRepository;
//    private final ToolWorkFlowNodeRepository toolWorkFlowNodeRepository;
//
//    @Autowired
//    public ToolWorkFlowNodeService(ToolRepository toolRepository, ToolConsequenceRepository toolConsequenceRepository, ToolWorkFlowNodeRepository toolWorkFlowNodeRepository) {
//        this.toolRepository = toolRepository;
//        this.toolConsequenceRepository = toolConsequenceRepository;
//        this.toolWorkFlowNodeRepository = toolWorkFlowNodeRepository;
//    }
//
//    //保存
//    public ToolWorkFlowNodeEntity saveToolWorkFlowNode(ToolWorkFlowNodeEntity toolWorkFlowNodeAgrs) {
//        if (StringUtils.isEmpty(toolWorkFlowNodeAgrs.getNodeName())) {
//            throw new RuntimeException(ApplicationMessages.TOOL_WORK_FLOW_NODE_NAME_NOT_NULL);
//        }
//        return toolWorkFlowNodeRepository.save(toolWorkFlowNodeAgrs);
//    }
////
////    //通过id删除工具工作业务流节点
////    public ToolWorkFlowNodeEntity deletedToolWorkFlowNodeById(String toolWorkFlowNodeId) {
////        ToolWorkFlowNodeEntity toolWorkFlowNodeEntity = getToolWorkFlowNodeById(toolWorkFlowNodeId);
////        //通过工具工作业务流删除绘图
////        for (ToolDrawingNodeEntity toolDrawingNodeEntity : toolDrawingNodeService.getToolDrawingNodeByToolWorkFlowNode(toolWorkFlowNodeEntity)) {
////            toolDrawingNodeRepository.delete(toolDrawingNodeEntity);
////        }
////        toolWorkFlowNodeRepository.delete(toolWorkFlowNodeEntity);
////        return toolWorkFlowNodeEntity;
////    }
//
//    //根据工具删除工作业务流节点
//    public List<ToolWorkFlowNodeEntity> cleanToolWorkFlowNodeByToolEntity(ToolEntity toolEntity) {
//        List<ToolWorkFlowNodeEntity> toolWorkFlowNodeEntityList = getToolWorkFlowNodeByToolEntity(toolEntity);
//        for (ToolWorkFlowNodeEntity toolWorkFlowNodeEntity : toolWorkFlowNodeEntityList) {
//            //删除工作业务流节点
//
//            toolWorkFlowNodeEntity.setToolEntity(null);
//            toolWorkFlowNodeRepository.save(toolWorkFlowNodeEntity);
//            //todo:有问题，没删除
//        }
//        return toolWorkFlowNodeEntityList;
//    }
//
//    public List<ToolWorkFlowNodeEntity> getToolWorkFlowNodeByToolEntity(ToolEntity toolEntity) {
//        return toolWorkFlowNodeRepository.findByToolEntity(toolEntity);
//    }
//
//    //根据id绑定工具
//    public ToolWorkFlowNodeEntity bindToolById(String toolWorkFlowNodeId, ToolEntity toolEntity) {
//        ToolWorkFlowNodeEntity toolWorkFlowNodeEntity = getToolWorkFlowNodeById(toolWorkFlowNodeId);
//        //先判断工具是否被绑定过
//        boolean ifBind = toolEntity.isIfBind();
//        System.out.println("获取的值为：" + ifBind);
//        if (!ifBind == false) {
//            throw new RuntimeException(ApplicationMessages.The_TOOL_HAS_BEEN_BOUND + toolEntity);
//        }
//        toolEntity.setIfBind(true);
//        toolRepository.save(toolEntity);
//        toolWorkFlowNodeEntity.setToolEntity(toolEntity);
//        return toolWorkFlowNodeRepository.save(toolWorkFlowNodeEntity);
//    }
//
//    //根据id绑定工具成果
//    public ToolWorkFlowNodeEntity bindToolConsequenceById(String toolWorkFlowNodeId, ToolConsequenceEntity toolConsequenceEntity) {
//        ToolWorkFlowNodeEntity toolWorkFlowNodeEntity = getToolWorkFlowNodeById(toolWorkFlowNodeId);
//        //先判断工具是否被绑定过
//        boolean ifBind = toolConsequenceEntity.isIfBind();
//        System.out.println("获取的值为：" + ifBind);
//        if (!ifBind == false) {
//            throw new RuntimeException(ApplicationMessages.The_TOOL_CONSEQUENCE_HAS_BEEN_BOUND + toolConsequenceEntity);
//        }
//        toolConsequenceEntity.setIfBind(true);
//        toolConsequenceRepository.save(toolConsequenceEntity);
//        toolWorkFlowNodeEntity.setToolConsequenceEntity(toolConsequenceEntity);
//        return toolWorkFlowNodeRepository.save(toolWorkFlowNodeEntity);
//    }
//
//    //解绑工具
//    public ToolWorkFlowNodeEntity unbindToolById(String toolWorkFlowNodeId, ToolEntity toolEntity) {
//        ToolWorkFlowNodeEntity toolWorkFlowNodeEntity = getToolWorkFlowNodeById(toolWorkFlowNodeId);
//        toolEntity.setIfBind(false);
//        toolRepository.save(toolEntity);
//        toolWorkFlowNodeEntity.setToolEntity(null);
//        return toolWorkFlowNodeRepository.save(toolWorkFlowNodeEntity);
//    }
//
//    //解绑工具成果
//    public ToolWorkFlowNodeEntity unbindToolConsequenceById(String toolWorkFlowNodeId, ToolConsequenceEntity toolConsequenceEntity) {
//        ToolWorkFlowNodeEntity toolWorkFlowNodeEntity = getToolWorkFlowNodeById(toolWorkFlowNodeId);
//        toolConsequenceEntity.setIfBind(false);
//        toolConsequenceRepository.save(toolConsequenceEntity);
//        toolWorkFlowNodeEntity.setToolConsequenceEntity(null);
//        return toolWorkFlowNodeRepository.save(toolWorkFlowNodeEntity);
//    }
//
//    public ToolWorkFlowNodeEntity getToolWorkFlowNodeById(String toolWorkFlowNodeId) {
//        if (!hasToolWorkFlowNodeById(toolWorkFlowNodeId)) {
//            throw new RuntimeException(ApplicationMessages.TOOL_WORK_FLOW_NODE_ID_NOT_FOUND + toolWorkFlowNodeId);
//        }
//        return toolWorkFlowNodeRepository.findById(toolWorkFlowNodeId).get();
//    }
//
//    public boolean hasToolWorkFlowNodeById(String toolWorkFlowNodeId) {
//        if (StringUtils.isEmpty(toolWorkFlowNodeId)) {
//            return false;
//        }
//        return toolWorkFlowNodeRepository.existsById(toolWorkFlowNodeId);
//    }
//}
