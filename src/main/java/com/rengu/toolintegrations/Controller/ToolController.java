package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 17:21
 */
@RestController
@RequestMapping(value = "/tool")
public class ToolController {
    private final ToolFileService toolFileService;
    private final ToolService toolService;
    private final UserService userService;

    @Autowired
    public ToolController(ToolFileService toolFileService, ToolService toolService, UserService userService) {
        this.toolFileService = toolFileService;
        this.toolService = toolService;
        this.userService = userService;
    }

    //创建工具
    @ApiOperation("创建工具")
    @PostMapping(value = "{userId}/user/saveTool")
    public ResultEntity saveTool(@PathVariable(value = "userId")String userId, ToolEntity toolArge){
        return ResultUtils.build(toolService.saveTool(userService.getUserById(userId),toolArge));
    }

    @ApiOperation("通过工具id删除工具")
    @DeleteMapping(value = "toolId}/deleteTool")
    public ResultEntity deleteToolById(@PathVariable(value = "toolId") String toolId){
        return ResultUtils.build(toolService.deleteToolById(toolId));
    }

    // 根据id和父节点Id创建文件
    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolId}/uploadfiles")
    public ResultEntity saveComponentFilesByParentNodeAndComponent(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolFileService.saveToolFilesByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }

    @ApiOperation("根据工具名称和文件名称组合查询")
    @GetMapping(value = "/{userId}/fuzzyQuery")
    public ResultEntity getComponentFuzzQueryById(String toolName ,String fileName,@PathVariable(value = "userId") String userId, Pageable pageable) {
        return ResultUtils.build(toolFileService.getToolFileFuzzQueryByToolNameOrByFileName(toolName,fileName,userId,pageable));
    }
}
