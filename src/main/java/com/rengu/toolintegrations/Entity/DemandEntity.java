package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/26 17:28
 * 需求工作流
 */
@Data
@Entity
public class DemandEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;//工作流名称
    private String type;//绑定的类型
    private String description;//描述
    private boolean deleted = false;//是否删除
    @OneToOne
    private ToolEntity toolEntity;//工具类
}
