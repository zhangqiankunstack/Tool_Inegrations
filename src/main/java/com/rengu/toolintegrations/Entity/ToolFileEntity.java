package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 16:36
 */
@Data
@Entity
public class ToolFileEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private String name;
    private String extension;          //后缀
    private boolean isFolder;          //是否为文件夹

    private String type;            //文件类型
    private String version;         //文件属于哪个版本

    @ManyToOne
    private FileEntity fileEntity;                  //文件
    @ManyToOne
    private ToolFileEntity parentNode;             //父类节点(文件夹)
    @ManyToOne
    private ToolEntity toolEntity;                  //工具类
}
