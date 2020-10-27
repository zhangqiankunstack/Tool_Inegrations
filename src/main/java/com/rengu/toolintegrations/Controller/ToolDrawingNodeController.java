package com.rengu.toolintegrations.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Service.ToolConsequenceService;
import com.rengu.toolintegrations.Service.ToolDrawingNodeService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Service.WorkflowCanvasService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 11:20
 */
@RestController
@RequestMapping(value = "/toolDrawingNode")
@Api(tags = {"工具流程节点控制层"})
public class ToolDrawingNodeController {
    private final ToolDrawingNodeService toolDrawingNodeService;
    private final ToolService toolService;
    private final WorkflowCanvasService workflowCanvasService;
    private final ToolConsequenceService toolConsequenceService;

    @Autowired
    public ToolDrawingNodeController(ToolDrawingNodeService toolDrawingNodeService, ToolService toolService, WorkflowCanvasService workflowCanvasService, ToolConsequenceService toolConsequenceService) {
        this.toolDrawingNodeService = toolDrawingNodeService;
        this.toolService = toolService;
        this.workflowCanvasService = workflowCanvasService;
        this.toolConsequenceService = toolConsequenceService;
    }

    //绑定工具以及工具成果
    @ApiOperation("绑定工具以及工具成果")
    @PostMapping(value = "/{workFlowCancasId}/bindtoolAndtoolConsequence")
    public ResultEntity bindtoolAndtoolConsequence(@PathVariable(value = "workFlowCancasId") String workFlowCancasId, @RequestBody @Valid String ids) {
        WorkflowCanvasEntity workflowCanvasEntity = workflowCanvasService.getWorkFlowCanvasById(workFlowCancasId);
        List<ToolDrawingNodeEntity> toolDrawingNodeEntityList = null;
        JSONArray jsonArrayList = JSONArray.parseArray(ids);
        for (Object obj : jsonArrayList) {
            toolDrawingNodeEntityList = new ArrayList<>();
            JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            String toolId = jsonObject.getString("toolId");
            String toolConId = jsonObject.getString("toolConId");
            String toolDrawingNodeId = jsonObject.getString("toolDrawingNodeId");
            ToolDrawingNodeEntity toolDrawingNodeEntity = new ToolDrawingNodeEntity();
            //TODO：id需要设置一下
            //toolDrawingNodeEntity.setId(toolDrawingNodeId);
            toolDrawingNodeEntity.setCoordinate(toolDrawingNodeId);//坐标位置
            ToolEntity toolEntity = null;
            if (toolId != null) {
                toolEntity = toolService.getToolByIds(toolId);
            }
            ToolConsequenceEntity toolConsequenceEntity = toolConsequenceService.getToolConsequence(toolConId);
            toolDrawingNodeEntity.setToolConsequenceEntity(toolConsequenceEntity);
            toolDrawingNodeEntity.setToolEntity(toolEntity);
            toolDrawingNodeEntity.setWorkflowCanvasEntity(workflowCanvasEntity);
            toolDrawingNodeEntityList.add(toolDrawingNodeEntity);
            toolDrawingNodeService.saveToolDrawingNode(toolDrawingNodeEntity);
        }
        return ResultUtils.build(toolDrawingNodeEntityList);
    }

    //根据id查詢流程節點
    @ApiOperation("根据id查詢流程節點")
    @GetMapping(value = "/{toolDrawingNodeId}/findToolDrawingNode")
    public ResultEntity findDrawingNodeById(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId) {
        return ResultUtils.build(toolDrawingNodeService.findDrawingNodeById(toolDrawingNodeId));
    }


    //根据工具绘图节点id删除流程节点
    @ApiOperation("根据工具绘图节点id以及工具成果id删除流程节点")
    @DeleteMapping(value = "/{toolDrawingNodeId}/deletedToolConsequence/{tooConsequenceId}/deletedToolDrawingNode")
    public ResultEntity deletedToolDrawingNode(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId, @PathVariable(value = "tooConsequenceId") String tooConsequenceId) throws IOException {
        return ResultUtils.build(toolDrawingNodeService.deletedToolDrawingNodeByWorkFlowIdAndToolConseId(toolDrawingNodeId, toolConsequenceService.getToolConsequenceById(tooConsequenceId)));
    }

    //绑定工具
    @ApiOperation("绑定工具")
    @PostMapping(value = "/{toolDrawingNodeId}/{toolId}/bind")
    public ResultEntity bindToolById(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId, @PathVariable(value = "toolId") String toolId) {
        return ResultUtils.build(toolDrawingNodeService.bindToolById(toolDrawingNodeId, toolService.getToolById(toolId)));
    }

//    //绑定工具成果
//    @ApiOperation("绑定工具成果文件")
//    @PostMapping(value = "/{toolDrawingNodeId}/toolConsequence/{toolConsequenceId}/bind")
//    public ResultEntity bindToolConsequenceById(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId, @PathVariable(value = "toolConsequenceId") String toolConsequenceId) {
//        return ResultUtils.build(toolDrawingNodeService.bindToolConsequenceById(toolDrawingNodeId, toolConsequenceId));
//    }

//    //解绑工具
//    @ApiOperation("解绑工具")
//    @PostMapping(value = "/{toolDrawingNodeId}/{toolId}/unbind")
//    public ResultEntity unbindToolById(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId, @PathVariable(value = "toolId") String toolId) {
//        return ResultUtils.build(toolDrawingNodeService.unbindToolById(toolDrawingNodeId, toolService.getToolById(toolId)));
//    }

//    //解绑工具成果
//    @ApiOperation("解绑工具成果文件")
//    @PostMapping(value = "/{toolDrawingNodeId}/toolConsequence/{toolConsequenceId}/unbind")
//    public ResultEntity unbindToolConsequenceById(@PathVariable(value = "toolDrawingNodeId") String toolDrawingNodeId, @PathVariable(value = "toolConsequenceId") String toolConsequenceId) {
//        return ResultUtils.build(toolDrawingNodeService.unbindToolConsequenceById(toolDrawingNodeId, toolConsequenceId));
//    }
}
