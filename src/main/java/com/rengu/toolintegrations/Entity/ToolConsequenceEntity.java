package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/31 16:31
 * 工具使用成果类
 */
@Data
@Entity
public class ToolConsequenceEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern ="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;
//    private String version;      //版本号
//    private String type;        //工具类型
    private boolean deleted = false;//是否删除
    private boolean ifBind = false;//是否被绑定
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(columnDefinition = "longtext",length = 0)
//    private String description;                //描述
//
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(columnDefinition = "longtext",length = 0)
//    private String NewFeatures;                //新特性

    @ManyToOne
    private UserEntity userEntity;//工具所属
}
