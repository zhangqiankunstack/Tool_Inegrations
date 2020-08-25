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
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-09-05 13:07
 * 日志记录实体类
 **/

@Data
@Entity
public class DeployLogEntity implements Serializable {

    @Id
    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime = new Date();
    private Date finishTime;            //结束时间
    private long totalFileSize;         //总文件大小
    private long totalSendSize;         //总发送大小
    private double speed;
    private double progress;            //进展
    private boolean complete = true;       //是否完成
    private String message;             //信息
}
