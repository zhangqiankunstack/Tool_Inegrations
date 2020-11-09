package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.ToolConsequenceFileService;
import com.rengu.toolintegrations.Service.ToolConsequenceService;
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
 * Date: 2020/9/1 8:48
 */
@RestController
@RequestMapping(value = "/ToolConsequenceFile")
@Api(tags = {"工具成果文件"})
public class ToolConsequenceFileController {
    private final ToolConsequenceFileService toolConsequenceFileService;

    @Autowired
    public ToolConsequenceFileController(ToolConsequenceFileService toolConsequenceFileService) {
        this.toolConsequenceFileService = toolConsequenceFileService;
    }

    //根据成果文件id删除工程文件
    @ApiOperation("根据成果文件id删除工程文件")
    @DeleteMapping(value = "{toolConsequenceFileId}/deleted")
    public ResultEntity deletedToolConsequenceFileById(@PathVariable(value = "toolConsequenceFileId") String toolConsequenceFileId) throws IOException {
        return ResultUtils.build(toolConsequenceFileService.deletedToolConsequenceFileById(toolConsequenceFileId));
    }

    //下载工具成果文件
    @GetMapping(value = "/{toolConsequenceFileId}/export")
    @ApiOperation("通过工具文件id下载工具成果文件")
    public void exportToolConsequenceFileByToolAchievements(@PathVariable(value = "toolConsequenceFileId") String toolConsequenceFileId, HttpServletResponse httpServletResponse) throws IOException {
        File exportFile = toolConsequenceFileService.exportConsequenceFileById(toolConsequenceFileId);
        String mimeType = URLConnection.guessContentTypeFromName(exportFile.getName()) == null ? "application/octet-stream" : URLConnection.guessContentTypeFromName(exportFile.getName());
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + new String(exportFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        httpServletResponse.setContentLengthLong(exportFile.length());
        // 文件流输出
        IOUtils.copy(new FileInputStream(exportFile), httpServletResponse.getOutputStream());
        httpServletResponse.flushBuffer();
    }
}
