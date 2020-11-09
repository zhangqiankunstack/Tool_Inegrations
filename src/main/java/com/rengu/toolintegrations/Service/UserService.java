package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.RoleEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.UserRepository;
import com.rengu.toolintegrations.Utils.ApplicationConfig;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 打包返回用户信息给security做用户校验使用（2）
 *
 * @program: Tool_integrations
 * @author: Zhangqiankun
 * @create: 2020-09-22 17:05
 **/

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * UserDetailsService中loadUserByUsername方法里面可以用来获取数据
     * 库中的密码然后打包返回用户信息给security做用户校验使用，
     * 后者校验如果与登录的密码match，如果成功，返回UserDetail对象（用户信息对象）
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }

    //保存普通用户
    public UserEntity saveDefaultUser(UserEntity userEntity) {
        return saveUser(userEntity, roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME));
    }

    //保存管理员用户
    public UserEntity saveAdminUser(UserEntity userEntity) {
        return saveAdmin(userEntity, roleService.getRoleByName(ApplicationConfig.DEFAULT_USER_ROLE_NAME), roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME));
    }

    // 保存用户
    @CachePut(value = "User_Cache", key = "#userEntity.id")
    public UserEntity saveUser(UserEntity userEntity, RoleEntity... roleEntities) {
        if (userEntity == null) {
            throw new RuntimeException(ApplicationMessages.USER_ARGS_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new RuntimeException(ApplicationMessages.USER_USERNAME_ARGS_NOT_FOUND);
        }
        if (hasUserByUsername(userEntity.getUsername())) {
            throw new RuntimeException(ApplicationMessages.USER_USERNAME_EXISTED + userEntity.getUsername());
        }
//        if (hasUserByUsername(userEntity.getRealName())) {
//            throw new RuntimeException(ApplicationMessages.USER_USERNAME_EXISTED + userEntity.getRealName());
//        }
//        if (StringUtils.isEmpty(userEntity.getPassword())) {
//            throw new RuntimeException(ApplicationMessages.USER_PASSWORD_ARGS_NOT_FOUND);
//        }
        //userEntity.setPassword(new BCryptPasswordEncoder().encode(userEntity.getPassword()));
        userEntity.setPassword(new BCryptPasswordEncoder().encode(ApplicationConfig.DEFAULT_USER_PASSWORD));//设置初始化密码
        Set<RoleEntity> roleEntitySet = userEntity.getRoleEntities() == null ? new HashSet<>() : userEntity.getRoleEntities();
        roleEntitySet.addAll(Arrays.asList(roleEntities));
        userEntity.setRoleEntities(roleEntitySet);
        return userRepository.save(userEntity);
    }

    // 自动生成管理员用户
    @CachePut(value = "User_Cache", key = "#userEntity.id")
    public UserEntity saveAdmin(UserEntity userEntity, RoleEntity... roleEntities) {
        if (userEntity == null) {
            throw new RuntimeException(ApplicationMessages.USER_ARGS_NOT_FOUND);
        }
        if (StringUtils.isEmpty(userEntity.getUsername())) {
            throw new RuntimeException(ApplicationMessages.USER_USERNAME_ARGS_NOT_FOUND);
        }
        if (hasUserByUsername(userEntity.getUsername())) {
            throw new RuntimeException(ApplicationMessages.USER_USERNAME_EXISTED + userEntity.getUsername());
        }
        userEntity.setRealName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME);
        userEntity.setPassword(new BCryptPasswordEncoder().encode(ApplicationConfig.DEFAULT_ADMIN_PASSWORD));//设置初始化密码
        Set<RoleEntity> roleEntitySet = userEntity.getRoleEntities() == null ? new HashSet<>() : userEntity.getRoleEntities();
        roleEntitySet.addAll(Arrays.asList(roleEntities));
        userEntity.setRoleEntities(roleEntitySet);
        return userRepository.save(userEntity);
    }

    // 根据Id删除用户
    @CacheEvict(value = "User_Cache", key = "#userId")
    public UserEntity deleteUserById(String userId) {
        UserEntity userEntity = getUserById(userId);
        userEntity.setIfDeleted(true);
        return userRepository.save(userEntity);
    }

    // 根据Id恢复用户
    @CacheEvict(value = "User_Cache", key = "#userId")
    public UserEntity restoreUserById(String userId) {
        UserEntity userEntity = getUserById(userId);
        userEntity.setIfDeleted(false);
        return userRepository.save(userEntity);
    }

    //根据id修改用户信息
    public UserEntity updateUserById(String userId, UserEntity userAgrs) {
        boolean isModifiedName = false;
        boolean isModifRealName = false;
        UserEntity userEntity = getUserById(userId);
        if (!StringUtils.isEmpty(userAgrs.getUsername()) && !userEntity.getUsername().equals(userAgrs.getUsername())) {
            isModifiedName = true;
        }
        if (!StringUtils.isEmpty(userAgrs.getRealName())) {
            isModifRealName = true;
        }
        if (isModifiedName) {
            userEntity.setUsername(userAgrs.getUsername());
        }
        if (isModifRealName) {
            userEntity.setRealName(userAgrs.getRealName());
        }
        return userRepository.save(userEntity);
    }


    // 管理员重置密码
    @CachePut(value = "User_Cache", key = "#userId")
    public UserEntity resetUserPasswordById(String userId) {
        UserEntity userEntity = getUserById(userId);
        userEntity.setPassword(new BCryptPasswordEncoder().encode(ApplicationConfig.DEFAULT_USER_PASSWORD));
        return userRepository.save(userEntity);
    }

    // 根据Id修改用户密码
    @CachePut(value = "User_Cache", key = "#userId")
    public UserEntity updateUserPasswordById(String userId, String password) {
        UserEntity userEntity = getUserById(userId);
        if (password != "" && password != null) {
            userEntity.setPassword(new BCryptPasswordEncoder().encode(password));
        }
        return userRepository.save(userEntity);
    }

    //根据用户名模糊查询
    public List<UserEntity> fuzzyFindUserByName(String userName, boolean ifDeleted) {
        return userRepository.findAllByUserNameAndIfDeleted(userName, ifDeleted);
    }

    // 根据Id升级用户
    @CachePut(value = "User_Cache", key = "#userId")
    public UserEntity userUpgradeById(String userId) {
        UserEntity userEntity = getUserById(userId);
        if (hasAdminAuthorityByUser(userEntity)) {
            throw new RuntimeException(ApplicationMessages.USER_HAS_ADMIN_ROLE);
        }
        userEntity.getRoleEntities().add(roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME));
        return userRepository.save(userEntity);
    }

    // 根据Id降级用户
    @CachePut(value = "User_Cache", key = "#userId")
    public UserEntity userDegradeById(String userId) {
        UserEntity userEntity = getUserById(userId);
        if (!hasAdminAuthorityByUser(userEntity)) {
            throw new RuntimeException(ApplicationMessages.USER_NOT_HAS_ADMIN_ROLE);
        }
        userEntity.getRoleEntities().remove(roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME));
        return userRepository.save(userEntity);
    }

    // 根据用户名查询用户是否存在
    public boolean hasUserByUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            return false;
        }
        return userRepository.existsByUsername(username);
    }

    // 根据Id查询用户是否存在
    public boolean hasUserById(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        }
        return userRepository.existsById(userId);
    }

    // 检查用户是否具有管理员权限
    public boolean hasAdminAuthorityByUser(UserEntity userEntity) {
        return userEntity.getRoleEntities().contains(roleService.getRoleByName(ApplicationConfig.DEFAULT_ADMIN_ROLE_NAME));
    }

    // 根据用户名查询用户
    public UserEntity getUserByUsername(String username) {
        if (!hasUserByUsername(username)) {
            throw new UsernameNotFoundException(ApplicationMessages.USER_USERNAME_NOT_FOUND + username);
        }
        return userRepository.findByUsername(username).get();
    }

    // 根据用户Id查询用户
    public UserEntity getUserById(String userId) {
        if (!hasUserById(userId)) {
            throw new RuntimeException(ApplicationMessages.USER_ID_NOT_FOUND + userId);
        }
        return userRepository.findById(userId).get();
    }

    // 查询所有用户
    public Page<UserEntity> getUsers(boolean ifDeleted, Pageable pageable) {
        return userRepository.findAllByIfDeleted(ifDeleted, pageable);
    }

    public UserEntity deleteUserEntity(UserEntity userEntity) {
        userEntity.getAuthorities().remove(userEntity);
        userRepository.delete(userEntity);
        return userEntity;
    }

    //管理员分配不同用户对不同工具的下载权限
    public UserEntity updateUserLimitByUserId(String userId, String downloadRights) {
        UserEntity userEntity = getUserById(userId);
        userEntity.setDownloadRights(downloadRights);
        return userRepository.save(userEntity);
    }
}

