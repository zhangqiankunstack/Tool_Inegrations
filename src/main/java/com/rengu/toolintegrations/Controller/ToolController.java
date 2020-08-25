package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ToolController(ToolFileService toolFileService, ToolService toolService) {
        this.toolFileService = toolFileService;
        this.toolService = toolService;
    }

    // 根据id和父节点Id创建文件
    @PostMapping(value = "/{toolId}/uploadfiles")
    public ResultEntity saveComponentFilesByParentNodeAndComponent(@PathVariable(value = "toolId") String toolId, @RequestHeader(value = "parentNodeId", required = false, defaultValue = "") String parentNodeId, @RequestBody List<FileMetaEntity> fileMetaEntityList) {
        return ResultUtils.build(toolFileService.saveToolFilesByParentNodeAndTool(toolService.getToolById(toolId), parentNodeId, fileMetaEntityList));
    }
}
