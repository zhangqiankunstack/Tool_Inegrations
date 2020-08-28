package com.rengu.toolintegrations.Utils;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:04
 **/


public class ApplicationMessages {

    // 用户相关提示信息
    public static final String USER_ARGS_NOT_FOUND = "未发现用户参数";
    public static final String USER_USERNAME_ARGS_NOT_FOUND = "用户名称参数不存在或不合法";
    public static final String USER_USERNAME_NOT_FOUND = "未发现该用户名称：";
    public static final String USER_USERNAME_EXISTED = "该用户名称已存在：";
    public static final String USER_PASSWORD_ARGS_NOT_FOUND = "用户密码参数不存在或不合法。";
    public static final String USER_PASSWORD_NOT_FOUND = "未发现该用户密码：";
    public static final String USER_PASSWORD_EXISTED = "该用户密码已存在：";
    public static final String USER_ID_ARGS_NOT_FOUND = "用户ID参数不存在或不合法。";
    public static final String USER_ID_NOT_FOUND = "未发现该用户ID：";
    public static final String USER_ID_EXISTED = "该用户ID已存在：";
    public static final String USER_HAS_ADMIN_ROLE = "该用户已具备管理员权限";
    public static final String USER_NOT_HAS_ADMIN_ROLE = "该用户不具备管理员权限";

    // 角色相关提示信息
    public static final String ROLE_ARGS_NOT_FOUND = "未发现角色参数";
    public static final String ROLE_NAME_ARGS_NOT_FOUND = "角色名称参数不存在或不合法：";
    public static final String ROLE_NAME_NOT_FOUND = "未发现该角色名称：";
    public static final String ROLE_NAME_EXISTED = "该角色名称已存在：";

    // 需求相关提示信息
    public static final String DEMAND_ID_ARGS_NOT_FOUND = "需求Id或参数不存在或不合法";
    public static final String WORKFLOW_NAME_NOT_NULL = "工作流名称不可为空";
    public static final String WORKFLOW_TYPE_NOT_NULL_OR_ARGS_NOT_FOUND = "工作流类型不可为空或不合法";



    // 工具相关提示信息

    public static final String TOOL_NAME_EXISTED = "该组件名已存在：";
    public static final String COMPONENT_VERSION_ARGS_NOT_FOUND = "组件版本参数不存在或不合法";

    public static final String TOOL_ID_NOT_FOUND = "未发现该工具Id：";
    public static final String TOOL_NAME_AND_VERSION_EXISTED = "该工具已存在：";


    //工具成果相关提示信息
    public static final String TOOL_AVHIEVEMENTS_NAME_AND_TYPE_AND_VERSION_EXIST="工具成果名称已存在";
    public static final String TOOL_AVHIEVEMENTS_NAME_NOT_NULL="工具成果名称不能为空";
    public static final String TOOL_AVHIEVEMENTS_NAME_ID_NOT_FOUND="工具成果ID未找到";
    public static final String TOOL_AVHIEVEMENTS_FILE_NAME_ID_NOT_FOUND="工具成果ID未找到";


    // 文件块相关提示信息
    public static final String FILE_CHUNK_ARGS_NOT_FOUND = "文件块不存在或不合法";
    public static final String FILE_CHUNK_NOT_FOUND = "未发现该文件块：";
    public static final String FILE_CHUNK_EXISTED = "该文件块已存在：";
    public static final String FILE_MD5_ARGS_NOT_FOUND = "文件MD5不存在或不合法";
    public static final String FILE_MD5_NOT_FOUND = "未发现该文件MD5：";
    public static final String FILE_MD5_EXISTED = "该文件MD5已存在：";
    public static final String FILE_ID_NOT_FOUND = "未发现该文件Id：";


    // 工具文件相关提示信息
    public static final String TOOL_FILE_ID_NOT_FOUND = "未发现该工具文件Id：";


    //操作日志提示消息
    public static final String USERACTIONLOG_ID_NOT_FOUND = "未发现该操作日志ID：";
}
