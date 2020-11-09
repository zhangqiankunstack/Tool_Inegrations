package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.ToolComment;
import com.rengu.toolintegrations.Service.ToolCommentService;
import com.rengu.toolintegrations.Service.ToolFileAndUserService;
import com.rengu.toolintegrations.Service.ToolService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ToolCommentController
 * @Description 工具评论
 * @Author Zhangqiankun
 * @Date 2020/8/28 9:26
 */
@RestController
@RequestMapping(value = "/toolComment")
@Api(tags = {"工具评论"})
public class ToolCommentController {
    private final ToolCommentService toolCommentService;
    private final UserService userService;
    private final ToolFileAndUserService toolFileAndUserService;
    private final ToolService toolService;

    public ToolCommentController(ToolCommentService toolCommentService, UserService userService, ToolFileAndUserService toolFileAndUserService, ToolService toolService) {
        this.toolCommentService = toolCommentService;
        this.userService = userService;
        this.toolFileAndUserService = toolFileAndUserService;
        this.toolService = toolService;
    }

    //新增评论
    @ApiOperation(value = "添加工具评论")
    @PostMapping(value = "/saveToolComment/{userId}/toolId/{toolId}")
    public ResultEntity saveToolComment(@PathVariable(value = "userId") String userId,@PathVariable(value = "toolId") String toolId ,@RequestBody ToolComment toolCommentArgs) {
        return ResultUtils.build(toolCommentService.saveToolComment(userService.getUserById(userId),toolService.getToolById(toolId),toolCommentArgs));
    }

    //查询平均分
    @ApiOperation(value = "查询平均分")
    @GetMapping (value = "/{toolId}/avgAvgStarGrade")
    public ResultEntity getAvgStarGrade(@PathVariable(value = "toolId")String toolId) {
        return ResultUtils.build(toolCommentService.getAvgStarGrade(toolId));
    }

//    @ApiOperation(value = "查看评论")
//    @GetMapping(value = "/findToolCommentByToolFileId/{toolFileId}")
//    public ResultEntity findToolCommentByToolFileId(@PathVariable(value = "toolFileId") String toolFileId, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
//        return ResultUtils.build(toolCommentService.findToolCommentByToolFileId(toolFileId, pageable));
//    }

     @ApiOperation(value = "查看当前工具下的评论")
    @GetMapping(value = "/findToolCommentByToolFileId/{toolId}")
    public ResultEntity findToolCommentByToolFileId(@PathVariable(value = "toolId") String toolId) {
        return ResultUtils.build(toolCommentService.findToolCommentByToolId(toolService.getToolById(toolId)));
    }

    //判断用户是否下载过文件
    @ApiOperation(value = "判断当前用户是否下载过当前文件")
    @GetMapping(value = "/hasExistByToolIdAndUserId/{toolId}/{userId}")
    public ResultEntity hasExistByToolFileIdAndUserId(@PathVariable(value = "toolId") String toolId, @PathVariable(value = "userId") String userId) {
        return ResultUtils.build(toolFileAndUserService.hasExistByToolIdAndUserId(toolId, userId));
    }
}
