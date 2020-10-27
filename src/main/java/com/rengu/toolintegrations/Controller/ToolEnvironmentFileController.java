package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.ToolEnvironmentFileService;
import com.rengu.toolintegrations.Service.ToolService;
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

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 17:21
 */
@RestController
@RequestMapping(value = "/toolAchievements")
@Api(tags = {"工具环境文件"})
public class ToolEnvironmentFileController {
    private final ToolEnvironmentFileService toolEnvironmentFileService;
    private final ToolService toolService;

    public ToolEnvironmentFileController(ToolEnvironmentFileService toolEnvironmentFileService, ToolService toolService) {
        this.toolEnvironmentFileService = toolEnvironmentFileService;
        this.toolService = toolService;
    }

//    @PostMapping(value = "/saveAntennaByAids/{userId}")
//    @ApiOperation(value = "添加工具环境")
//    public ResultEntity saveAntennaByids(@PathVariable(value = "userId") String userId, @RequestBody ToolEnvironmentEntity toolAchievementsArgs) {
//        return ResultUtils.build(toolEnvironmentService.saveToolAchievements(userService.getUserById(userId), toolAchievementsArgs));
//    }

//    @ApiOperation("查询上传文件下的环境文件")
//    @PostMapping(value = "/findToolAchievements/{toolFileId}")
//    public ResultEntity saveToolAchievementsFileByParentNodeAndTool(@PathVariable(value = "toolFileId") String toolFileId) {
//        return ResultUtils.build(toolEnvironmentService.findToolAchievements(toolFileId));
//    }

    //根据id清除工具环境文件
    @ApiOperation("根据id清除工具环境文件")
    @DeleteMapping(value = "/{toolEvironmentFilesId}/deletedToolEnvironmentFile")
    public ResultEntity deletedToolEnvironmentFileById(@PathVariable(value = "toolEvironmentFilesId") String toolEvironmentFilesId) throws IOException {
        return ResultUtils.build(toolEnvironmentFileService.deleteToolEnvironmentFileByToolEnvironmentFile(toolEnvironmentFileService.getToolEnvironmentFileById(toolEvironmentFilesId)));
    }

    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolId}/uploadfiles")
    public ResultEntity saveToolAchievementsFileByParentNodeAndTool(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolEnvironmentFileService.saveToolAchievementsFileByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }

//todo：测试代码用户9月21日
//    @ApiOperation("根据id查看文件")
//    @PostMapping(value = "/{toolId}/findToolAchievementsFilesByToolAchievementsId")
//    public ResultEntity findToolAchievementsFilesByToolAchievementsId(@PathVariable(value = "toolId") String toolId, Pageable pageable) {
//        return ResultUtils.build(toolEnvironmentFileService.findToolEnvironmentFilesByToolAchievementsId(toolId, pageable));
//    }


    //下载工具环境文件
    @GetMapping(value = "/{toolEnvironmentFileId}/export")
    @ApiOperation("下载工具环境文件")
    public void exportToolEnvironmentFileByToolAchievements(@PathVariable(value = "toolEnvironmentFileId") String toolEnvironmentFileId, HttpServletResponse httpServletResponse) throws IOException {
        File exportFile = toolEnvironmentFileService.exportEnvironmentFileById(toolEnvironmentFileId);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
