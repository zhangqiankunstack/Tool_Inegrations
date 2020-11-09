//package com.rengu.toolintegrations.Controller;
//
//import com.rengu.toolintegrations.Entity.ResultEntity;
//import com.rengu.toolintegrations.Entity.ToolWorkFlowNodeEntity;
//import com.rengu.toolintegrations.Service.ToolConsequenceService;
//import com.rengu.toolintegrations.Service.ToolService;
//import com.rengu.toolintegrations.Service.ToolWorkFlowNodeService;
//import com.rengu.toolintegrations.Utils.ResultUtils;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Author: Zhangqiankun
// * Date: 2020/9/1 9:45
// * 业务流程
// */
//@Api(tags = {"业务流程"})
//@RestController
//@RequestMapping(value = "/toolWorkFlowNode")
//public class ToolWorkFlowNodeController {
//    private final ToolWorkFlowNodeService toolWorkFlowNodeService;
//    private final ToolService toolService;
//    private final ToolConsequenceService toolConsequenceService;
//
//    @Autowired
//    public ToolWorkFlowNodeController(ToolWorkFlowNodeService toolWorkFlowNodeService, ToolService toolService, ToolConsequenceService toolConsequenceService) {
//        this.toolWorkFlowNodeService = toolWorkFlowNodeService;
//        this.toolService = toolService;
//        this.toolConsequenceService = toolConsequenceService;
//    }
//
//    //添加节点
//    @ApiOperation("添加业务节点")
//    @PostMapping(value = "/saveToolWorkFlowNode")
//    public ResultEntity saveToolWorkFlowNode(ToolWorkFlowNodeEntity toolWorkFlowNodeAgrs) {
//        return ResultUtils.build(toolWorkFlowNodeService.saveToolWorkFlowNode(toolWorkFlowNodeAgrs));
//    }
//
////    //通过id删除业务节点
////    @ApiOperation("通过id删除业务节点")
////    @DeleteMapping(value = "/{toolWorkFlowNodeId}/deletedToolWorkFlowNodeById")
////    public ResultEntity deletedToolWorkFlowNodeById(@PathVariable(value = "toolWorkFlowNodeId")String toolWorkFlowNodeId){
////        return ResultUtils.build(toolWorkFlowNodeService.deletedToolWorkFlowNodeById(toolWorkFlowNodeId));
////    }
//
//    //绑定工具
//    @ApiOperation("绑定工具")
//    @PostMapping(value = "/{toolWorkFlowNodeId}/{toolId}/bind")
//    public ResultEntity bindToolById(@PathVariable(value = "toolWorkFlowNodeId") String toolWorkFlowNodeId, @PathVariable(value = "toolId") String toolId) {
//        return ResultUtils.build(toolWorkFlowNodeService.bindToolById(toolWorkFlowNodeId, toolService.getToolById(toolId)));
//    }
//
//    //绑定工具成果
//    @ApiOperation("绑定工具成果文件")
//    @PostMapping(value = "/{toolWorkFlowNodeId}/toolConsequence/{toolConsequenceId}/bind")
//    public ResultEntity bindToolConsequenceById(@PathVariable(value = "toolWorkFlowNodeId") String toolWorkFlowNodeId, @PathVariable(value = "toolConsequenceId") String toolConsequenceId) {
//        return ResultUtils.build(toolWorkFlowNodeService.bindToolConsequenceById(toolWorkFlowNodeId, toolConsequenceService.getToolConsequenceById(toolConsequenceId)));
//    }
//
//    //解绑工具
//    @ApiOperation("解绑工具")
//    @PostMapping(value = "/{toolWorkFlowNodeId}/{toolId}/unbind")
//    public ResultEntity unbindToolById(@PathVariable(value = "toolWorkFlowNodeId") String toolWorkFlowNodeId, @PathVariable(value = "toolId") String toolId) {
//        return ResultUtils.build(toolWorkFlowNodeService.unbindToolById(toolWorkFlowNodeId, toolService.getToolById(toolId)));
//    }
//
//    //解绑工具成果
//    @ApiOperation("解绑工具成果文件")
//    @PostMapping(value = "/{toolWorkFlowNodeId}/toolConsequence/{toolConsequenceId}/unbind")
//    public ResultEntity unbindToolConsequenceById(@PathVariable(value = "toolWorkFlowNodeId") String toolWorkFlowNodeId, @PathVariable(value = "toolConsequenceId") String toolConsequenceId) {
//        return ResultUtils.build(toolWorkFlowNodeService.unbindToolConsequenceById(toolWorkFlowNodeId, toolConsequenceService.getToolConsequenceById(toolConsequenceId)));
//    }
//
//}
