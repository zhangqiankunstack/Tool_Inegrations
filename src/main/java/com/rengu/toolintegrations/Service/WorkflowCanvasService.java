package com.rengu.toolintegrations.Service;

import com.alibaba.fastjson.JSONArray;
import com.rengu.toolintegrations.Entity.ToolDrawingNodeEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;

import com.rengu.toolintegrations.Entity.WorkflowCanvasEntity;
import com.rengu.toolintegrations.Repository.WorkflowCanvasRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 17:38
 */
@Service
public class WorkflowCanvasService {
    private final WorkflowCanvasRepository workflowCanvasRepository;
    private final ToolDrawingNodeService toolDrawingNodeService;

    @Autowired
    public WorkflowCanvasService(WorkflowCanvasRepository workflowCanvasRepository, ToolDrawingNodeService toolDrawingNodeService) {
        this.workflowCanvasRepository = workflowCanvasRepository;
        this.toolDrawingNodeService = toolDrawingNodeService;
    }

    //保存工作流画布
    public WorkflowCanvasEntity saveWorkFlowCancas(UserEntity userEntity, WorkflowCanvasEntity workflowCanvasAgrs) {
        workflowCanvasAgrs.setUserEntity(userEntity);
        return workflowCanvasRepository.save(workflowCanvasAgrs);
    }

    //修改
    public WorkflowCanvasEntity updateToolDrawingNode(String workFlowCanvasId, String agrs) {
        JSONArray jsonArrayList = JSONArray.parseArray(agrs);
        System.out.println(workFlowCanvasId);
        WorkflowCanvasEntity workflowCanvasEntity = getWorkFlowCanvasById(workFlowCanvasId);
        workflowCanvasEntity.setAgrs(jsonArrayList.get(0).toString());
        workflowCanvasEntity.setConnect(jsonArrayList.get(1).toString());
        workflowCanvasRepository.save(workflowCanvasEntity);
        return workflowCanvasEntity;
    }

    //删除画布
    public WorkflowCanvasEntity deleteWorkFlowCanvasById(String workFlowCanvasId) {
        WorkflowCanvasEntity workflowCanvasEntity = getWorkFlowCanvasById(workFlowCanvasId);
        workflowCanvasEntity.setDeleted(true);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deletedTime = format.format(date);
        workflowCanvasEntity.setDeletedTime(deletedTime);
        return workflowCanvasRepository.save(workflowCanvasEntity);
    }

    //恢复画布
    public WorkflowCanvasEntity restoreWorkFlowCanvas(String workFlowCanvasId) {
        WorkflowCanvasEntity workflowCanvasEntity = getWorkFlowCanvasById(workFlowCanvasId);
        workflowCanvasEntity.setDeleted(false);
        return workflowCanvasRepository.save(workflowCanvasEntity);
    }

    //查询所有绘图节点
    public List<WorkflowCanvasEntity> findAllWorkFlow(boolean deleted,boolean IsVisible) {
        //Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return workflowCanvasRepository.findByDeletedAndIsVisible(deleted,IsVisible);
    }

    //查询user下的所有绘图节点
    public List<WorkflowCanvasEntity> getWorkFlowNodeAllByUserAndDeleted(UserEntity user, boolean deleted) {
        return workflowCanvasRepository.findAllByUserEntityAndDeleted(user, deleted);
    }

    //通过id清除流程
    public WorkflowCanvasEntity cleanWorkFlowById(String workflowCanvasId) {
        WorkflowCanvasEntity workflowCanvasEntity = getWorkFlowCanvasById(workflowCanvasId);
        //通过画布删除绘图节点
        toolDrawingNodeService.cleanToolDrawingNodeByWorkflowCanvasEntity(workflowCanvasEntity);
        workflowCanvasRepository.delete(workflowCanvasEntity);
        return workflowCanvasEntity;
    }

    public WorkflowCanvasEntity getWorkFlowCanvasById(String workFlowCanvasId) {
        if (!hasWorkFlowCanvasById(workFlowCanvasId)) {
            throw new RuntimeException(ApplicationMessages.WORK_FLOW_CANVAS_ID_NOT_FOUND + workFlowCanvasId);
        }
        return workflowCanvasRepository.findById(workFlowCanvasId).get();
    }

    public boolean hasWorkFlowCanvasById(String workFlowCanvasId) {
        if (StringUtils.isEmpty(workFlowCanvasId)) {
            return false;
        }
        return workflowCanvasRepository.existsById(workFlowCanvasId);
    }

    //根据工具删除工具绘图节点
    public List<ToolDrawingNodeEntity> cleanToolDrawingNodeByToolEntity(ToolEntity toolEntity) {

        return toolDrawingNodeService.getToolDrawingNodeByToolEntity(toolEntity);
    }

    public WorkflowCanvasEntity updateWorkFlowCanvas(String workFlowCanvasId, WorkflowCanvasEntity workflowCanvasAgrs) {
        WorkflowCanvasEntity workflowCanvasEntity = getWorkFlowCanvasById(workFlowCanvasId);
        workflowCanvasEntity.setName(workflowCanvasAgrs.getName());
        workflowCanvasEntity.setDescription(workflowCanvasAgrs.getDescription());
        workflowCanvasEntity.setDeleted(workflowCanvasAgrs.isDeleted());
        workflowCanvasEntity.setIsVisible(workflowCanvasAgrs.isIsVisible());
        return workflowCanvasRepository.save(workflowCanvasEntity);
    }

    //根据画布名是否删除是否可见模糊查询
    public List<WorkflowCanvasEntity> fuzzyworkFlowCanvasByName(String workFlowCanvasName, boolean deleted, boolean isVisible) {
        return workflowCanvasRepository.findAllByNameAndDeletedAndIsVisible(workFlowCanvasName, deleted, isVisible);
    }
    //根据画布名是否删除是否可见模糊查询
    public List<WorkflowCanvasEntity> fuzzyworkFlowCanvasByNameAndDeleted(String workFlowCanvasName, boolean deleted,String userId) {
        return workflowCanvasRepository.findAllByNameAndDeletedAndIsVisibleAndUserId(workFlowCanvasName, deleted,userId);
    }

    public List<WorkflowCanvasEntity> cleanWorkFlowCanvasByUserEntity(UserEntity userEntity) {
        List<WorkflowCanvasEntity> workflowCanvasEntityList = getWorkFlowAllByUserEntity(userEntity);
        for(WorkflowCanvasEntity workflowCanvasEntity:workflowCanvasEntityList){
            //通过流程中的节点信息
            toolDrawingNodeService.cleanToolDrawingNodeByWorkflowCanvasEntity(workflowCanvasEntity);
            //todo :可能会有问题，因为删除流程管理不成功，是因为流程
            workflowCanvasRepository.delete(workflowCanvasEntity);
        }
        return workflowCanvasEntityList;
    }

    //通过用户获取所有的流程管理
    public List<WorkflowCanvasEntity> getWorkFlowAllByUserEntity(UserEntity userEntity){
        return workflowCanvasRepository.findAllByUserEntity(userEntity);
    }
}
