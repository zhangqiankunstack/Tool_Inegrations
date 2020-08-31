package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.ToolFileAndUserService;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
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

/**
 * Author: Zhagnqiankun
 * Date: 2020/8/27 11:01
 */
@RestController
@RequestMapping(value = "/toolFile")
@Api(value = "工具文件")
public class ToolFileController {
    private final ToolFileService toolFileService;

    @Autowired
    public ToolFileController(ToolFileService toolFileService, ToolFileAndUserService toolFileAndUserService) {
        this.toolFileService = toolFileService;
    }

    //通过工具id查工具文件
    @ApiOperation("通过工具id查工具文件")
    @GetMapping(value = "/{toolId}/toolAllFile")
    public ResultEntity getToolFilesByToolId(@PathVariable(value = "toolId") String toolId, Pageable pageable) {
        return ResultUtils.build(toolFileService.getToolFilesByToolId(toolId, pageable));
    }

    //根据工具文件id导出工具文件
    @ApiOperation("根据工具文件id导出工具文件")
    @GetMapping(value = "/{toolFileId}/export/{userId}/userexport")
    public void exportToolFileById(@PathVariable(value = "toolFileId") String toolFileId,@PathVariable(value = "userId") String userId ,HttpServletResponse httpServletResponse) throws IOException {
        File exportFile = toolFileService.exportToolFileById(toolFileId,userId);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
