package com.rengu.toolintegrations.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @program: Tool_integrations
 * @author: Zhangqiankun
 * @create: 2020-09-22 16:59
 **/
public class ApplicationConfig {

    // 默认角色
    public static final String DEFAULT_ADMIN_ROLE_NAME = "admin";           //管理员
    public static final String DEFAULT_USER_ROLE_NAME = "user";             //普通用户
    public static final String DEFAULT_PROVIDER_ROLE_NAME = "provider";     //提供者
    // 默认管理员用户
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin";
    // 默认使用者用户
//    public static final String DEFAULT_USER_USERNAME = "user";
    public static final String DEFAULT_USER_PASSWORD = "123456";
    // 默认使用者用户
//    public static final String DEFAULT_PROVIDER_USERNAME = "provider";
//    public static final String DEFAULT_PROVIDER_PASSWORD = "123456";


    // 服务器连接地址、端口
    public static final int TCP_RECEIVE_PORT = 6006;
    public static final int UDP_RECEIVE_PORT = 6005;
    public static final int UDP_SEND_PORT = 3090;
    public static final int TCP_DEPLOY_PORT = 3091;
    public static final String SERVER_CAST_ADDRESS = "224.10.10.15";
    public static final int SERVER_BROAD_CAST_PORT = 3087;
    public static final int SERVER_MULTI_CAST_PORT = 3087;

    // 文件块保存路径
    public static final String CHUNKS_SAVE_PATH = FormatUtils.formatPath(FileUtils.getTempDirectoryPath() + File.separator + "OMS" + File.separator + "CHUNKS");
    // 文件保存路径
    public static final String FILES_SAVE_PATH = FormatUtils.formatPath(FileUtils.getUserDirectoryPath() + File.separator + "OMS" + File.separator + "FILES");

}