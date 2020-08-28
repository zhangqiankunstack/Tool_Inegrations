package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.DemandEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.DemandService;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


/**
 * Author: Zhangqiankun
 * Date: 2020/8/26 17:44
 */
@RestController
@RequestMapping(value = "/demand")
@Api(value = "需求工作流")
public class DemandController {
    private final DemandService demandService;
    private final ToolFileService toolFileService;
    private final ToolService toolService;

    @Autowired
    public DemandController(DemandService demandService, ToolFileService toolFileService, ToolService toolService) {
        this.demandService = demandService;
        this.toolFileService = toolFileService;
        this.toolService = toolService;
    }

    //添加工作流
    @ApiOperation("根据工具保存工作流")
    @PostMapping(value = "/addWorkflow")
    public ResultEntity addWorkFlow(@RequestBody DemandEntity demandArgs){
        return ResultUtils.build(demandService.saveWorkFlow(demandArgs));
    }

    //绑定工具
    @ApiOperation("绑定工具")
    @PostMapping(value = "/{demandId}/toolsBaseinfo/{toolsBaseinfoId}/bind")
    public ResultEntity bindToolsBaseinfoById(@PathVariable(value = "demandId")String demandId, @PathVariable(value = "toolsBaseinfoId") String toolsBaseinfoId){
        return ResultUtils.build(demandService.bindToolsBaseinfoById(demandId,toolsBaseinfoId));
    }

    //取消绑定
    @ApiOperation("取消绑定")
    @PostMapping(value = "/{demandId}/toolsBaseinfo/unbind")
    public ResultEntity unbindToolsBaseinfoById(@PathVariable(value = "demandId")String demandId){
        return ResultUtils.build(demandService.unbindToolsBaseinfoById(demandId));
    }

    //查询需求绑定的工具类下的所有文件
    @ApiOperation("查询需求绑定下的工具文件类下的所有文件")
    @GetMapping(value ="/{demandId}/tools/files")
    public ResultEntity findAllFiles(@PathVariable(value = "demandId")String demandId, Pageable pageable){
        String toolId = demandService.getDemandById(demandId).getToolEntity().getId();
        //toolService.getToolById(toolId).getId();
        //通过工具id获取工具文件
        //List<ToolFileEntity> toolFileEntityList = toolFileService

        return ResultUtils.build(toolFileService.getToolFilesByToolId(toolId,pageable));
    }
}
