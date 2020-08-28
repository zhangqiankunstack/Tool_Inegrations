package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.ToolFileService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ToolFileController(ToolFileService toolFileService) {
        this.toolFileService = toolFileService;
    }

    //通过工具id查工具文件
    @ApiOperation("通过工具id查工具文件")
    @GetMapping(value = "/{toolId}/toolAllFile")
    public ResultEntity getToolFilesByToolId(@PathVariable(value = "toolId")String toolId, Pageable pageable){
        return ResultUtils.build(toolFileService.getToolFilesByToolId(toolId,pageable));
    }
}
