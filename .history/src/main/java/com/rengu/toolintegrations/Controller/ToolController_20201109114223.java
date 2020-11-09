package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Service.*;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
=======
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
>>>>>>> 8e09eddcc39544d2a11154e6bb903ee9adea5989
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
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
@RequestMapping(value = "/tool")
@Api(tags = {"工具"})
public class ToolController {
    private final ToolFileService toolFileService;
    private final ToolService toolService;
    private final UserService userService;
    private final ToolEnvironmentFileService toolEnvironmentFileService;
    private final ToolFileAndUserService toolFileAndUserService;

    @Autowired
    public ToolController(ToolFileService toolFileService, ToolService toolService, UserService userService, ToolEnvironmentFileService toolEnvironmentFileService, ToolFileAndUserService toolFileAndUserService) {
        this.toolFileService = toolFileService;
        this.toolService = toolService;
        this.userService = userService;
        this.toolEnvironmentFileService = toolEnvironmentFileService;
        this.toolFileAndUserService = toolFileAndUserService;
    }

    //创建工具
    @ApiOperation("创建工具")
    @PostMapping(value = "{userId}/user/saveTool")
    public ResultEntity saveTool(@PathVariable(value = "userId") String userId, ToolEntity toolArge) {
        return ResultUtils.build(toolService.saveTool(userService.getUserById(userId), toolArge));
    }

    //通过用户id查询工具数量



    //根据id查询工具信息
    @ApiOperation("根据id查询信息")
    @GetMapping(value = "/{toolId}/getToolEntity")
    public ResultEntity getToolEntityById(@PathVariable(value = "toolId") String toolId) {
        return ResultUtils.build(toolService.getToolById(toolId));
    }

    //根据工具id删除工具
    @ApiOperation("通过工具id删除工具")
    @DeleteMapping(value = "/{toolId}/deleteTool")
    public ResultEntity deleteToolById(@PathVariable(value = "toolId") String toolId) {
        return ResultUtils.build(toolService.deleteToolById(toolId));
    }

    //根据id恢复工具
    @ApiOperation("根据工具id恢复工具")
    @PatchMapping(value = "/{toolId}/restore")
    public ResultEntity restoreToolById(@PathVariable(value = "toolId") String toolId) {
        return ResultUtils.build(toolService.restoreToolById(toolId));
    }

    //通过id清除工具
    @ApiOperation("根据工具id清除工具")
    @DeleteMapping(value = "/{toolId}/clean")
    public ResultEntity cleanToolById(@PathVariable(value = "toolId") String toolId) throws IOException {
        return ResultUtils.build(toolService.cleanToolById(toolId));
    }

    //根据工具id修改工具
    @ApiOperation("根据工具id修改工具")
    @PatchMapping(value = "/{toolId}")
    public ResultEntity updateToolById(@PathVariable(value = "toolId") String toolId, @RequestBody ToolEntity toolAgrs) {
        return ResultUtils.build(toolService.updateToolById(toolId, toolAgrs));
    }

    //根据id和父节点ID创建文件夹
    @ApiOperation("根据id和父节点ID创建文件夹")
    @PostMapping(value = "/{toolId}/createFolder")
    public ResultEntity saveToolFileByParentNodeAndTool(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId,@RequestBody ToolFileEntity toolFileEntity) {
        return ResultUtils.build(toolFileService.saveToolFileByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, toolFileEntity));
    }

    // 根据id和父节点Id创建文件
    @ApiOperation("根据id和父节点Id创建文件")
    @PostMapping(value = "/{toolId}/uploadfiles")
    public ResultEntity saveToolFilesByParentNodeAndTool(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolFileService.saveToolFilesByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }

    //查询整个库中的工具
    @ApiOperation("查询整个库中的工具")
    @GetMapping(value = "/getAllTool")
    public ResultEntity getAllTool(@PathParam(value = "deleted") boolean deleted) {
        return ResultUtils.build(toolService.getToolAll(deleted));
    }

    //通过工具id查工具文件
    @ApiOperation("通过工具id查工具文件")
    @GetMapping(value = "/{toolId}/toolAllFile")
    public ResultEntity getToolFilesByToolId(@PathVariable(value = "toolId") String toolId, Pageable pageable) {
        return ResultUtils.build(toolFileService.getToolFilesByToolId(toolId, pageable));
    }

    //TODO：路径两个参数，不合理。 张乾坤 2020.10.12
    @ApiOperation("根据id导出所有的工具文件和环境文件")
    @GetMapping(value = "/{toolId}/export/{userId}")
    public void exportToolFileByTool(@PathVariable(value = "toolId") String toolId, @PathVariable(value = "userId") String userId, HttpServletResponse httpServletResponse) throws IOException {
        toolFileAndUserService.saveToolAndUser(toolId, userId);
        UserEntity user = userService.getUserById(userId);
        //导出文件
        File exportFile = toolFileService.exportToolFileByTool(toolService.getToolById(toolId), user);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    //通过工具名称模糊查询
    @ApiOperation("通过工具名称模糊查询")
    @PreAuthorize(value = "hasRole('admin')")
    @GetMapping(value = "/fuzzyToolByToolName")
    public ResultEntity fuzzyToolByToolName(@PathParam(value = "toolName") String toolName) {
        return ResultUtils.build(toolService.fuzzyToolByToolName(toolName));
    }

    @ApiOperation("根据id导出所有的环境文件")
    @GetMapping(value = "/{toolId}/exportToolEnvironment/{userId}")
    public void exportToolEnvironmentById(@PathVariable(value = "toolId") String toolId, @PathVariable(value = "userId") String userId, HttpServletResponse httpServletResponse) throws IOException {
        UserEntity user = userService.getUserById(userId);
        //导出文件
        File exportFile = toolEnvironmentFileService.exportToolEnvironmentByTool(toolService.getToolById(toolId), user);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    @ApiOperation("根据id导出所有的工具文件")
    @GetMapping(value = "/{toolId}/exportToolFile/{userId}")
    public void exportToolFileByToolEntity(@PathVariable(value = "toolId") String toolId, @PathVariable(value = "userId") String userId, HttpServletResponse httpServletResponse) throws IOException {
        UserEntity user = userService.getUserById(userId);
        //导出文件
        File exportFile = toolFileService.exportToolFileByToolEntity(toolService.getToolById(toolId), user);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }

    //通过用户id查询工具数量
    @ApiOperation("根据id导出所有的工具文件")
    @GetMapping(value = "/findToolCount/{userId}")
    public ResultEntity findToolCount(@PathVariable(value = "userId") String userId){
        return ResultUtils.build(toolService.findToolCount(userId));
    }

    //工具管理，下拉框选择
    @ApiOperation("工具管理，下拉框选择")
    @GetMapping(value = "/findToolByType")
    public ResultEntity findToolByType(){
        return ResultUtils.build(toolService.findToolByType());
    }

}
