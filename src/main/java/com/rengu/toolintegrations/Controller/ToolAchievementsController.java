package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.ToolAchievements;
import com.rengu.toolintegrations.Service.ToolAchievementsFileService;
import com.rengu.toolintegrations.Service.ToolAchievementsService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(value = "/toolAchievements")
@Api(tags={"工具成果"})
public class ToolAchievementsController {
    private final ToolAchievementsService toolAchievementsService;
    private final ToolAchievementsFileService achievementsFileService;
    private final UserService userService;
    private final ToolService toolService;

    public ToolAchievementsController(ToolAchievementsService toolAchievementsService, ToolAchievementsFileService achievementsFileService, UserService userService, ToolService toolService) {
        this.toolAchievementsService = toolAchievementsService;
        this.achievementsFileService = achievementsFileService;
        this.userService = userService;
        this.toolService = toolService;
    }

    @PostMapping(value = "/saveAntennaByAids/{userId}")
    @ApiOperation(value = "添加工具成果")
    public ResultEntity saveAntennaByAids(@PathVariable(value = "userId")String userId,@RequestBody ToolAchievements toolAchievementsArgs) {
        return ResultUtils.build(toolAchievementsService.saveToolAchievements(userService.getUserById(userId),toolAchievementsArgs));
    }

    @ApiOperation("查询上传文件下的成果文件")
    @PostMapping(value = "/findToolAchievements/{toolFileId}")
    public ResultEntity saveToolAchievementsFileByParentNodeAndTool(@PathVariable(value = "toolFileId") String toolFileId) {
        return ResultUtils.build(toolAchievementsService.findToolAchievements(toolFileId));
    }

    // 根据id和父节点Id创建文件
//    @ApiOperation("根据id和父节点Id创建文件")
//    @PostMapping(value = "/{toolAchievementsId}/uploadfiles")
//    public ResultEntity saveToolAchievementsFileByParentNodeAndTool(@PathVariable(value = "toolAchievementsId") String toolAchievementsId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
//        return ResultUtils.build(achievementsFileService.saveToolAchievementsFileByParentNodeAndTool(toolAchievementsService.getToolAchievementsById(toolAchievementsId), parentNodeId, fileMetaEntityList));
//    }

    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolId}/uploadfiles")
    public ResultEntity saveToolAchievementsFileByParentNodeAndTool(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(achievementsFileService.saveToolAchievementsFileByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }

    //
    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolId}/findToolAchievementsFilesByToolAchievementsId")
    public ResultEntity findToolAchievementsFilesByToolAchievementsId(@PathVariable(value = "toolId") String toolId, Pageable pageable) {
        return ResultUtils.build(achievementsFileService.findToolAchievementsFilesByToolAchievementsId(toolId,pageable));
    }


    //下载工具成果文件
    @GetMapping(value = "/{achievementsFilesId}/export")
    @ApiOperation("下载工具成果文件")
    public void exportToolAchievementsFileByToolAchievements(@PathVariable(value = "achievementsFilesId") String achievementsFilesId, HttpServletResponse httpServletResponse) throws IOException {
        File exportFile = achievementsFileService.exportAchievementsFileById(achievementsFilesId);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
