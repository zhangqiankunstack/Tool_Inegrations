package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ChunkEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.FileService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-27 16:50
 **/

@Slf4j
@RestController
@RequestMapping(value = "/files")
@Api(tags = {"上传文件"})
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // 检查文件块是否存在
    @ApiOperation("检查文件块是否存在")
    @GetMapping(value = "/chunks")
    public void hasChunk(HttpServletResponse httpServletResponse, ChunkEntity chunkEntity) {
        if (!fileService.hasChunk(chunkEntity)) {
            httpServletResponse.setStatus(HttpServletResponse.SC_GONE);
        }
    }

    // 检查文件块是否存在
    @ApiOperation("保存文件块是否存在")
    @PostMapping(value = "/chunks")
    public void saveChunk(ChunkEntity chunkEntity, @RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
        fileService.saveChunk(chunkEntity, multipartFile);
    }

    // 合并文件块
    @ApiOperation("合并文件块")
    @PostMapping(value = "/chunks/merge")
    public ResultEntity mergeChunks(ChunkEntity chunkEntity) throws IOException {
        return ResultUtils.build(fileService.mergeChunks(chunkEntity));
    }

    // 根据MD5检查文件是否存在
    @ApiOperation("根据MD5检查文件是否存在")
    @GetMapping(value = "/hasmd5")
    public ResultEntity hasFileByMD5(@RequestParam(value = "MD5") String MD5) {
        return ResultUtils.build(fileService.hasFileByMD5(MD5) ? fileService.getFileByMD5(MD5) : fileService.hasFileByMD5(MD5));
    }
}