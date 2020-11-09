package com.rengu.toolintegrations.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;


/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 16:56
 **/

@Data
public class ResultEntity<T> implements Serializable {

    private String id = UUID.randomUUID().toString();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime = new Date();
    private T data;

}
