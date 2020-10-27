package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Service.ToolConsequenceFileService;
import com.rengu.toolintegrations.Service.ToolConsequenceService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Date: 2020/8/31 17:13
 */
@RestController
@RequestMapping("/toolConsequence")
@Api(tags = {"工具成果"})
public class ToolConsequenceController {
    private final ToolConsequenceService toolConsequenceService;
    private final ToolConsequenceFileService toolConsequenceFileService;
    private final UserService userService;

    @Autowired
    public ToolConsequenceController(ToolConsequenceService toolConsequenceService, ToolConsequenceFileService toolConsequenceFileService, UserService userService) {
        this.toolConsequenceService = toolConsequenceService;
        this.toolConsequenceFileService = toolConsequenceFileService;
        this.userService = userService;
    }

//    //保存工具成果
//    @ApiOperation("保存工具成果")
//    @PostMapping("/{userId}/saveToolConsequence")
//    public ResultEntity saveToolConsequence(@PathVariable(value = "userId") String userId, ToolConsequenceEntity toolConsequenceAgrs) {
//        return ResultUtils.build(toolConsequenceService.saveToolConsequence(userService.getUserById(userId), toolConsequenceAgrs));
//    }

    // 根据id和父节点Id创建文件
    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolConsequenceName}/uploadfiles/{userId}")
    public ResultEntity saveToolFilesByParentNodeAndTool(@PathVariable(value = "toolConsequenceName") String toolConsequenceName, @PathVariable(value = "userId") String userId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolConsequenceFileService.saveToolFilesByParentNodeAndTool(toolConsequenceName, userId, parentNodeId, fileMetaEntityList));
    }

    //根据工具成果id和父类节点查询工具成果文件
    @ApiOperation("查询文件")
    @GetMapping(value = "/{toolConsequenceId}/files")
    public ResultEntity getToolConsequenceFileByParentNodeAndToolConsequence(@PathVariable(value = "toolConsequenceId") String toolConsequenceId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId) {
        return ResultUtils.build(toolConsequenceFileService.getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceService.getToolConsequenceById(toolConsequenceId), parentNodeId));
    }

    //根据成果id导出成果文件
    @ApiOperation("根据成果id导出成果文件")
    @GetMapping(value = "/{toolConsequenceId}/exportToolEnvironment/{userId}")
    public void exportToolEnvironmentById(@PathVariable(value = "toolConsequenceId") String toolConsequenceId, @PathVariable(value = "userId") String userId, HttpServletResponse httpServletResponse) throws IOException {
        UserEntity user = userService.getUserById(userId);
        //导出文件
        File exportFile = toolConsequenceFileService.exportToolConsequenceFileByToolConsequence(toolConsequenceService.getToolConsequenceById(toolConsequenceId), user);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
