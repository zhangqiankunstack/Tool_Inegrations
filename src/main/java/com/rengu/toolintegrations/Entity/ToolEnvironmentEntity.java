//package com.rengu.toolintegrations.Entity;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//
//import javax.persistence.*;
//import java.io.Serializable;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * Author: Zhangqiankun
// * Date: 2020/8/31 14:40
// * 工具环境类
// */
//@Entity
//@Data
//public class ToolEnvironmentEntity implements Serializable {
//    @Id
//    private String id = UUID.randomUUID().toString();
//    private String name;
//    private String type;
//    private String version;      //版本号
//    @Lob
//    @Basic(fetch = FetchType.LAZY)
//    @Column(columnDefinition = "longtext", length = 0)
//    private String description;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date createTime = new Date();
//    private boolean isVisible = true; //是否可见
//    @ManyToOne
//    private UserEntity userEntity;
//}
