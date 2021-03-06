package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Zhagnqiankun
 * Date: 2020/8/25 16:01
 * 工具类
 */
@Data
@Entity
public class ToolEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;
    private String version;      //版本号
    private String type;        //工具类型
    private boolean deleted = false;//是否删除
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longtext",length = 0)
    private String description;                //描述

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longtext",length = 0)
    private String NewFeatures;                //新特性

    @ManyToOne
    private UserEntity userEntity;//工具所属
}
