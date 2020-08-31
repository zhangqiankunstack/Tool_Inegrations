package com.rengu.toolintegrations.Utils;

import com.rengu.toolintegrations.Entity.RoleEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Service.RoleService;
import com.rengu.toolintegrations.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InterfaceAddress;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:09
 **/

@Slf4j
@Order(value = -1)
@Component
public class ApplicationInit implements ApplicationRunner {

    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public ApplicationInit(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        // 获取并打印本机IP地址
//        StringBuilder stringBuilder = new StringBuilder("|");
//        for (InterfaceAddress interfaceAddress : IPUtils.getLocalIPs()) {
//            stringBuilder.append(interfaceAddress.getAddress().toString().replace("/", "")).append("|");
//        }
//        log.info("OMS服务器-IP地址：" + stringBuilder.toString());
//        // 初始化文件保存路径
//        File file = new File(ApplicationConfig.FILES_SAVE_PATH);
//        log.info("OMS服务器-组件实体文件缓存存放路径：" + new File(ApplicationConfig.CHUNKS_SAVE_PATH).getAbsolutePath());
//        log.info("OMS服务器-组件实体文件存放路径：" + file.getAbsolutePath());
//        if (!file.exists()) {
//            FileUtils.forceMkdir(file);
//        }
        // 启动TCP消息接受线程
//        tcpReceiveThread.TCPMessageReceiver();
        // 启动UDP消息接受线程
//        udpReceiveThread.UDPMessageReceiver();
        // 初始化默认管理员角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME);
            roleEntity.setDescription("系统默认管理员角色");
            roleService.saveRole(roleEntity);
            log.info("OMS服务器-初始化系统默认管理员角色：" + roleEntity.getName());
        }
        //初始化默认用户角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(ApplicationConfig.DEFAULT_USER_ROLE_NAME);
            roleEntity.setDescription("系统默认用户角色");
            roleService.saveRole(roleEntity);
            log.info("OMS服务器-初始化系统默认用户角色：" + roleEntity.getName());
        }
        //初始化默认用户角色
        if (!roleService.hasRoleByName(ApplicationConfig.DEFAULT_PROVIDER_ROLE_NAME)) {
            RoleEntity roleEntity = new RoleEntity();
            roleEntity.setName(ApplicationConfig.DEFAULT_PROVIDER_ROLE_NAME);
            roleEntity.setDescription("系统默认提供者角色");
            roleService.saveRole(roleEntity);
            log.info("OMS服务器-初始化系统默认提供者角色：" + roleEntity.getName());
        }

        // 初始化管理员用户
        if (!userService.hasUserByUsername(ApplicationConfig.DEFAULT_ADMIN_USERNAME)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(ApplicationConfig.DEFAULT_ADMIN_USERNAME);
            userEntity.setPassword(ApplicationConfig.DEFAULT_ADMIN_PASSWORD);
            userService.saveAdminUser(userEntity);
            log.info("OMS服务器-初始化系统默认用户：" + userEntity.getUsername());
        }
        // 初始化提供者用户
        if (!userService.hasUserByUsername(ApplicationConfig.DEFAULT_USER_USERNAME)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(ApplicationConfig.DEFAULT_USER_USERNAME);
            userEntity.setPassword(ApplicationConfig.DEFAULT_USER_PASSWORD);
            userService.saveAdminUser(userEntity);
            log.info("OMS服务器-初始化系统默认用户：" + userEntity.getUsername());
        }
        // 初始化使用者用户
        if (!userService.hasUserByUsername(ApplicationConfig.DEFAULT_PROVIDER_USERNAME)) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(ApplicationConfig.DEFAULT_PROVIDER_USERNAME);
            userEntity.setPassword(ApplicationConfig.DEFAULT_PROVIDER_PASSWORD);
            userService.saveAdminUser(userEntity);
            log.info("OMS服务器-初始化系统默认用户：" + userEntity.getUsername());
        }


    }
}
