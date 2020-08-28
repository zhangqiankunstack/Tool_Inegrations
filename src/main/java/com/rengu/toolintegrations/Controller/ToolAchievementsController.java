package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.ToolAchievements;
import com.rengu.toolintegrations.Service.ToolAchievementsFileService;
import com.rengu.toolintegrations.Service.ToolAchievementsService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/toolAchievements")
@Api(value = "工具成果")
public class ToolAchievementsController {
    private final ToolAchievementsService toolAchievementsService;
    private final ToolAchievementsFileService achievementsFileService;
    private final UserService userService;

    public ToolAchievementsController(ToolAchievementsService toolAchievementsService, ToolAchievementsFileService achievementsFileService, UserService userService) {
        this.toolAchievementsService = toolAchievementsService;
        this.achievementsFileService = achievementsFileService;
        this.userService = userService;
    }

    @PostMapping(value = "/saveAntennaByAids/{userId}")
    @ApiOperation(value = "添加工具成果")
    public ResultEntity saveAntennaByAids(@PathVariable(value = "userId")String userId,@RequestBody ToolAchievements toolAchievementsArgs) {
        return ResultUtils.build(toolAchievementsService.saveToolAchievements(userService.getUserById(userId),toolAchievementsArgs));
    }

    // 根据id和父节点Id创建文件
    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolAchievementsId}/uploadfiles")
    public ResultEntity saveComponentFilesByParentNodeAndComponent(@PathVariable(value = "toolAchievementsId") String toolAchievementsId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(achievementsFileService.saveToolAchievementsFileByParentNodeAndTool(toolAchievementsService.getToolAchievementsById(toolAchievementsId), parentNodeId, fileMetaEntityList));
    }

    //下载工具成果文件

}
