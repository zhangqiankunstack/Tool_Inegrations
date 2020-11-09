package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/10 11:00
 * 绘图节点
 */
@Data
@Entity
public class ToolDrawingNodeEntity implements Serializable {
    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String Coordinate;      //  坐标位置
    @ManyToOne
    private WorkflowCanvasEntity workflowCanvasEntity;
    @ManyToOne
    private ToolEntity toolEntity;//绑定的工具
    @ManyToOne
    private ToolConsequenceEntity toolConsequenceEntity;//绑定的成果
}
