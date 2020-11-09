//package com.rengu.toolintegrations.Entity;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.ManyToOne;
//import java.io.Serializable;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * Author: Zhangqiankun
// * Date: 2020/9/1 9:08
// * 工作业务流节点（业务逻辑）
// */
//@Data
//@Entity
//public class ToolWorkFlowNodeEntity implements Serializable {
//    @Id
//    private String id = UUID.randomUUID().toString();
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date createTime = new Date();
//    private String nodeName;  //节点名称
//    private boolean ifBind = false;//是否被绑定
//    @ManyToOne
//    private ToolEntity toolEntity;//绑定的工具
//    @ManyToOne
//    private ToolConsequenceEntity toolConsequenceEntity;//绑定的成果
//}
