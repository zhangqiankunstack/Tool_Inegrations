package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResultEntity saveToolFilesByParentNodeAndTool(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolFileService.saveToolFilesByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }

    @ApiOperation("根据id导出所有的工具文件")
    @GetMapping(value = "/{toolId}/export/{userId}")
    public void exportToolFileByTool(@PathVariable(value = "toolId")String toolId, @PathVariable(value = "userId")String userId, HttpServletResponse httpServletResponse)throws IOException {
        UserEntity user = userService.getUserById(userId);
        //导出文件
        File exportFile = toolFileService.exportToolFileByTool(toolService.getToolById(toolId),user);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName())==null ? "application/octet-stream":URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
