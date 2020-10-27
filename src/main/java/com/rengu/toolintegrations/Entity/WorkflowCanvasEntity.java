package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 17:21
 * 工作流画布
 */
@Data
@Entity
public class WorkflowCanvasEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String description;
    private String name;
    private boolean IsVisible; //是否可见
    private boolean deleted = false;
    private String deletedTime;     //删除时间

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longtext",length = 0)
    private String agrs;        //坐标参数

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "longtext",length = 0)
    private String connect;     //连线参数
    @ManyToOne
    private UserEntity userEntity;
}
