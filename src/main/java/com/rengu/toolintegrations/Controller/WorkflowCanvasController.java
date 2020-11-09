package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.WorkflowCanvasEntity;
import com.rengu.toolintegrations.Service.ToolDrawingNodeService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Service.WorkflowCanvasService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 17:28
 */
@Api(tags = {"流程管理"})
@RestController
@RequestMapping(value = "/workFlowCanvas")
public class WorkflowCanvasController {
    private final WorkflowCanvasService workflowCanvasService;
    private final UserService userService;
    private final ToolDrawingNodeService toolDrawingNodeService;

    @Autowired
    public WorkflowCanvasController(WorkflowCanvasService workflowCanvasService, UserService userService, ToolDrawingNodeService toolDrawingNodeService) {
        this.workflowCanvasService = workflowCanvasService;
        this.userService = userService;
        this.toolDrawingNodeService = toolDrawingNodeService;
    }

    //添加流程
    @ApiOperation("添加流程")
    @PostMapping(value = "/{userId}/saveWorkFlowCancas")
    public ResultEntity saveWorkFlowCancas(@PathVariable(value = "userId") String userId, @RequestBody WorkflowCanvasEntity workflowCanvasAgrs) {
        return ResultUtils.build(workflowCanvasService.saveWorkFlowCancas(userService.getUserById(userId), workflowCanvasAgrs));
    }

    //通过画布id修改流程节点位置信息
    @ApiOperation("通过流程id修改流程节点位置信息")
    @PostMapping(value = "/{workFlowCanvasId}/updateToolDrawingNode")
    public ResultEntity updateToolDrawingNode(@PathVariable(value = "workFlowCanvasId") String workFlowCanvasId, @RequestBody @Valid String agrs) {
        return ResultUtils.build(workflowCanvasService.updateToolDrawingNode(workFlowCanvasId, agrs));
    }

    //通过画布id修改流程信息
    @ApiOperation("通过画布id修改流程信息")
    @PostMapping(value = "/{workFlowCanvasId}/updateWorkFlowCanvas")
    public ResultEntity updateWorkFlowCanvas(@PathVariable(value = "workFlowCanvasId") String workFlowCanvasId, @RequestBody WorkflowCanvasEntity workflowCanvasAgrs) {
        return ResultUtils.build(workflowCanvasService.updateWorkFlowCanvas(workFlowCanvasId, workflowCanvasAgrs));
    }

    //删除流程
    @ApiOperation("删除流程")
    @DeleteMapping(value = "/{workFlowCanvasId}/deletedById")
    public ResultEntity deleteWorkFlowCanvasById(@PathVariable(value = "workFlowCanvasId") String workFlowCanvasId) {
        return ResultUtils.build(workflowCanvasService.deleteWorkFlowCanvasById(workFlowCanvasId));
    }

    //恢复流程
    @ApiOperation("恢复流程")
    @PatchMapping(value = "/{workFlowCanvasId}/restore")
    public ResultEntity restoreWorkFlowCanvasById(@PathVariable(value = "workFlowCanvasId")String workFlowCanvasId){
        return ResultUtils.build(workflowCanvasService.restoreWorkFlowCanvas(workFlowCanvasId));
    }

    //清除流程
    @ApiOperation("清除流程")
    @DeleteMapping(value = "/{workFlowCanvasId}/cleanWorkFlowById")
    public ResultEntity cleanWorkFlowById(@PathVariable(value = "workFlowCanvasId")String workFlowCanvasId){
        return ResultUtils.build(workflowCanvasService.cleanWorkFlowById(workFlowCanvasId));
    }


    //通过流程id查询画布节点绑定信息
    @ApiOperation("通过流程id查询画布节点绑定信息")
    @GetMapping(value = "/{workFlowCanvasId}/selectToolDrawingNodeByWorkFlowCanvasId")
    public ResultEntity getToolDrawingNodeByWorkFlowCanvasId(@PathVariable(value = "workFlowCanvasId") String workFlowCanvasId) {
        return ResultUtils.build(toolDrawingNodeService.getToolDrawingNodeByWorkFlowCanvasId(workflowCanvasService.getWorkFlowCanvasById(workFlowCanvasId)));
    }

    @ApiOperation("查询所有公开性的流程")
    @GetMapping(value = "/findAllWorkFlow")
    public ResultEntity findAllWorkFlow(@PathParam(value = "deleted") boolean deleted,@PathParam(value = "IsVisible") boolean IsVisible) {
        return ResultUtils.build(workflowCanvasService.findAllWorkFlow(deleted,IsVisible));
    }

    //根据流程名是否删除是否可见模糊查询
    @ApiOperation("根据流程名是否删除是否可见模糊查询")
    @GetMapping(value = "/fuzzyworkFlowCanvasByName")
    public ResultEntity fuzzyworkFlowCanvasByName(@PathParam(value = "workFlowCanvasName")String workFlowCanvasName,@PathParam(value = "deleted")boolean deleted ,@PathParam(value = "isVisible")boolean isVisible){
        return ResultUtils.build(workflowCanvasService.fuzzyworkFlowCanvasByName(workFlowCanvasName,deleted,isVisible));
    }

    //根据流程名是否删除是否可见模糊查询
    @ApiOperation("根据流程名是否删除是否可见模糊查询")
    @GetMapping(value = "/fuzzyworkFlowCanvasByNameAndDeleted")
    public ResultEntity fuzzyworkFlowCanvasByName(@PathParam(value = "workFlowCanvasName")String workFlowCanvasName,@PathParam(value = "deleted")boolean deleted ,@PathParam(value = "userId") String userId){
        return ResultUtils.build(workflowCanvasService.fuzzyworkFlowCanvasByNameAndDeleted(workFlowCanvasName,deleted,userId));
    }
}
