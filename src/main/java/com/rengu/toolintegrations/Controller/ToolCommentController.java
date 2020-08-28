package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.ToolComment;
import com.rengu.toolintegrations.Service.ToolCommentService;
import com.rengu.toolintegrations.Service.ToolFileAndUserService;
import com.rengu.toolintegrations.Service.UserService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName ToolCommentController
 * @Description TODO
 * @Author yyc
 * @Date 2020/8/28 9:26
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/toolComment")
public class ToolCommentController {
    private final ToolCommentService toolCommentService;
    private final UserService userService;
    private final ToolFileAndUserService toolFileAndUserService;

    public ToolCommentController(ToolCommentService toolCommentService, UserService userService, ToolFileAndUserService toolFileAndUserService) {
        this.toolCommentService = toolCommentService;
        this.userService = userService;
        this.toolFileAndUserService = toolFileAndUserService;
    }

    //新增评论
    @PostMapping(value = "/saveToolComment/{userId}")
    @ApiOperation(value = "添加工具评论")
    public ResultEntity saveToolComment(@PathVariable(value = "userId") String userId, @RequestBody ToolComment toolCommentArgs) {
        return ResultUtils.build(toolCommentService.saveToolComment(userService.getUserById(userId), toolCommentArgs));
    }

    @GetMapping(value = "/findToolCommentByToolFileId/{toolFileId}")
    @ApiOperation(value = "查看评论")
    public ResultEntity findToolCommentByToolFileId(@PathVariable(value = "toolFileId") String toolFileId) {
        return ResultUtils.build(toolCommentService.findToolCommentByToolFileId(toolFileId));
    }

    //判断用户是否下载过文件
    @GetMapping(value = "/hasExistByToolFileIdAndUserId/{toolFileId}/{userId}")
    @ApiOperation(value = "判断当前用户是否下载过当前文件")
    public ResultEntity hasExistByToolFileIdAndUserId(@PathVariable(value = "toolFileId") String toolFileId, @PathVariable(value = "userId") String userId) {
        return ResultUtils.build(toolFileAndUserService.hasExistByToolFileIdAndUserId(toolFileId, userId));
    }

}
