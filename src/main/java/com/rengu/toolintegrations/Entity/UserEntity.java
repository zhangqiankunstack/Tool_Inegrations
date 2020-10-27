package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.*;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 16:53
 **/

@Data
@Entity
public class UserEntity implements UserDetails, Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String username;
    private String password;
    private String realName;                       // 姓名
    private boolean accountNonExpired = true;       //是否过期
    private boolean accountNonLocked = true;        //不被锁
    private boolean credentialsNonExpired = true;   //授权是否过期
    private boolean enabled = true;                 //是否可用
    private boolean ifDeleted = false;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roleEntities;


    /**
     * 手动添加用户权限
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (RoleEntity roleEntity : roleEntities) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roleEntity.getName()));
        }
        return grantedAuthorities;
    }
}
