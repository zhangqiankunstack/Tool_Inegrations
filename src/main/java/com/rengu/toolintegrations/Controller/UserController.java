package com.rengu.toolintegrations.Controller;

import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Service.*;
import com.rengu.toolintegrations.Utils.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.io.IOException;

/**
 * @program:
 * @author: Zhangqiankuun
 * @create: 2020-09-10 17:13
 **/


@RestController
@RequestMapping(value = "/users")
@Api(tags = {"用户"})
public class UserController {

    private final UserService userService;
    private final UserActionLogService userActionLogService;
    private final ToolService toolService;
    private final ToolFileService toolFileService;
    private final ToolEnvironmentFileService toolEnvironmentFileService;
    private final WorkflowCanvasService workflowCanvasService;
    private final ToolConsequenceFileService toolConsequenceFileService;
    private final ToolConsequenceService toolConsequenceService;
    private final ToolCommentService toolCommentService;

    @Autowired
    public UserController(UserService userService, UserActionLogService userActionLogService, ToolService toolService, ToolFileService toolFileService, ToolEnvironmentFileService toolEnvironmentFileService, WorkflowCanvasService workflowCanvasService, ToolConsequenceFileService toolConsequenceFileService, ToolConsequenceService toolConsequenceService, ToolCommentService toolCommentService) {
        this.userService = userService;
        this.userActionLogService = userActionLogService;
        this.toolService = toolService;
        this.toolFileService = toolFileService;
        this.toolEnvironmentFileService = toolEnvironmentFileService;
        this.workflowCanvasService = workflowCanvasService;
        this.toolConsequenceFileService = toolConsequenceFileService;
        this.toolConsequenceService = toolConsequenceService;
        this.toolCommentService = toolCommentService;
    }

    // 保存普通用户
    @ApiOperation("保存普通用户")
    @PostMapping(value = "/user")
//    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity saveDefaultUser(@RequestBody UserEntity userEntity) {
        return ResultUtils.build(userService.saveDefaultUser(userEntity));
    }

    //保存管理员用户
    @ApiOperation("保存管理员用户")
    @PostMapping(value = "/admin")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity saveAdminUser(UserEntity userEntity) {
        return ResultUtils.build(userService.saveAdminUser(userEntity));
    }

    // 清除用户
    @ApiOperation("清除用户")
    @DeleteMapping(value = "/{userId}/cleanUser")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity deleteUser(@PathVariable(value = "userId") String userId) throws IOException {
        UserEntity userEntity = userService.getUserById(userId);
        toolService.cleanToolByUserEntity(userEntity);
        workflowCanvasService.cleanWorkFlowCanvasByUserEntity(userEntity);
        toolConsequenceService.cleanToolConsequenceByUserEntity(userEntity);
        //删除此用户对工具的评论
        toolCommentService.cleanToolComentByUserEntity(userEntity);
        //删除角色表
        userService.deleteUserEntity(userEntity);
        return ResultUtils.build(userEntity);
    }

    // 删除用户
    @ApiOperation("删除用户")
    @DeleteMapping(value = "/{userId}")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity deleteUserById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.deleteUserById(userId));
    }

    // 恢复用户
    @ApiOperation("恢复用户")
    @PatchMapping(value = "/{userId}/restoreUser")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity restoreUserById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.restoreUserById(userId));
    }


    // 根据Id修改用户信息
    @ApiOperation("根据Id修改用户信息")
    @PatchMapping(value = "/{userId}/updateUserAgrs")
    public ResultEntity updatePasswordById(@PathVariable(value = "userId") String userId, @RequestBody UserEntity userAgrs) {
        return ResultUtils.build(userService.updateUserById(userId, userAgrs));
    }

    // 管理员重置密码
    @ApiOperation("管理员重置密码")
    @PreAuthorize(value = "hasRole('admin')")
    @PatchMapping(value = "/{userId}/resetPassword")
    public ResultEntity resetUserById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.resetUserPasswordById(userId));
    }

    //TODO：路径两个参数，不合理。 张乾坤 2020.10.12
    // 根据Id修改密码
    @ApiOperation("根据Id修改密码")
    @PatchMapping(value = "/{userId}/password/{password}/UpdatePassword")
    public ResultEntity updateUserById(@PathVariable(value = "userId") String userId, @PathVariable(value = "password") String password) {
        return ResultUtils.build(userService.updateUserPasswordById(userId, password));
    }

    // 根据Id升级用户
    @ApiOperation("根据Id升级用户")
    @PatchMapping(value = "/{userId}/upgrade")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity userUpgradeById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.userUpgradeById(userId));
    }

    // 根据Id降级用户
    @ApiOperation("根据Id降级用户")
    @PatchMapping(value = "/{userId}/degrade")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity userDegradeById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.userDegradeById(userId));
    }

    // 根据id查询用户
    @ApiOperation("根据id查询用户")
    @GetMapping(value = "/{userId}")
    public ResultEntity getUserById(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userService.getUserById(userId));
    }

    // 根据用户名判断用户是否存在
    @ApiOperation("根据用户名判断用户是否存在")
    @GetMapping(value = "/{userName}/hasName")
    public ResultEntity hsaUserName(@PathVariable(value = "userName") String userName) {
        return ResultUtils.build(userService.hasUserByUsername(userName));
    }

    // 查询所有用户
    @ApiOperation("查询所有用户")
    @GetMapping()
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity getUsers(@PathParam(value = "ifDeleted") boolean ifDeleted, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResultUtils.build(userService.getUsers(ifDeleted, pageable));
    }

    //通过用户名模糊查询
    @ApiOperation("通过用户名模糊查询")
    @GetMapping(value = "/fuzzyFind")
    public ResultEntity fuzzyFind(@PathParam(value = "userName") String userName, @PathParam(value = "ifDeleted") boolean ifDeleted) {
        return ResultUtils.build(userService.fuzzyFindUserByName(userName, ifDeleted));
    }

    // 根据Id查询用户操作日志
    @ApiOperation("根据Id查询用户操作日志")
    @GetMapping(value = "/{userId}/useractionlogs")
    public ResultEntity getUserActionLogsByUsername(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable(value = "userId") String userId) {
        return ResultUtils.build(userActionLogService.getUserActionLogsByUsername(pageable, userService.getUserById(userId).getUsername()));
    }

    //通过用户id和是否删除查询工具
    @ApiOperation("通过用户id和是否删除查询工具")
    @GetMapping(value = "/{userId}/findAllTool")
    public ResultEntity findAllToolByUserId(@PathVariable(value = "userId") String userId, @RequestParam(value = "deleted") boolean deleted, Pageable pageable) {
        return ResultUtils.build(toolService.getToolAllByDeletedAndUser(deleted, userService.getUserById(userId), pageable));
    }

    @ApiOperation("根据用户id，名称，删除类型，模糊查询")
    @GetMapping(value = "/fuzzyToolQuery")
    public ResultEntity getToolFuzzQueryByNameAndDeletedAndUserId(@PathParam(value = "userId") String userId, @PathParam(value = "toolName") String toolName, @PathParam(value = "deleted") boolean deleted, Pageable pageable) {
        return ResultUtils.build(toolService.getToolFuzzQueryByUserIdAndToolNameAndDeleted(toolName, deleted, userId, pageable));
    }

    @ApiOperation("根据工具类型和文件名称组合模糊查询")
    @GetMapping(value = "/fuzzyQuery")
    public ResultEntity getToolFuzzQueryById(@PathParam(value = "toolTpye") String toolTpye, @PathParam(value = "toolName") String toolName, boolean deleted, Pageable pageable) {
        return ResultUtils.build(toolService.getToolFuzzQueryByToolTypeOrByToolName(toolTpye, toolName, deleted, pageable));
    }

    //todo 查询工具数量，暂时不用
    //通过用户id查询所有的文件
//    @ApiOperation("通过id查询该用户所上传的工具文件")
//    @GetMapping(value = "/{userId}/findAllToolFiles")
//    public ResultEntity findAllToolFilesByUserId(@PathVariable(value = "userId") String userId) {
//        return ResultUtils.build(toolFileService.getToolAllByUser(userId));
//    }

    //通过id查询该用户所上传的环境文件
    @ApiOperation("通过id查询该用户所上传的环境文件")
    @GetMapping(value = "/{userId}/findAllToolEnvironmentFiles")
    public ResultEntity findAllToolEnvironmentFilesByUserId(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(toolEnvironmentFileService.getToolEnvironmentFileAllByUser(userId));
    }

    //TODO：路径两个参数，不合理。 张乾坤 2020.10.12
    //通过用户id查询所有的绘图节点
    @ApiOperation("通过用户id以及是否删除查询所有的绘图")
    @GetMapping(value = "/{userId}/deleted/{deleted}/findAllWorkFlowCanvas")
    public ResultEntity findAllWorkFlowNodes(@PathVariable(value = "userId") String userId, @PathVariable(value = "deleted") boolean deleted) {
        return ResultUtils.build(workflowCanvasService.getWorkFlowNodeAllByUserAndDeleted(userService.getUserById(userId), deleted));
    }

    //通过id查询该用户所上传的成果文件
    @ApiOperation("通过id查询该用户所上传的成果文件")
    @GetMapping(value = "/{userId}/findAllToolConsequenceFilessss")
    public ResultEntity findAllToolConsequencesFilesByUserId(@PathVariable(value = "userId") String userId) {
        return ResultUtils.build(toolConsequenceFileService.getToolConsequenceFileByUserId(userId));
    }

    //通过id查询该用户下所有的成果
    @ApiOperation("通过id查询该用户下所有的成果")
    @GetMapping(value = "/{userId}/findAllToolConsequenceFiles")
    public ResultEntity findAllToolConsequenceFilesByUserId(@PathVariable(value = "userId") String userId) {
        UserEntity userEntity = userService.getUserById(userId);
        return ResultUtils.build(toolConsequenceService.getToolConsequenceByUserEntity(userEntity));
    }

    //管理员分配不同用户对不同工具的下载权限
    @ApiOperation("管理员分配不同用户对不同工具的下载权限")
    @PatchMapping(value = "/updateUserLimitByUserId/{userId}/{downloadRights}")
    @PreAuthorize(value = "hasRole('admin')")
    public ResultEntity updateUserLimitByUserId(@PathVariable(value = "userId") String userId, @PathVariable(value = "downloadRights") String downloadRights) {
        return ResultUtils.build(userService.updateUserLimitByUserId(userId, downloadRights));
    }
}