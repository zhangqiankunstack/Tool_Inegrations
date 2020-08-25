package com.rengu.toolintegrations.Utils;

import com.rengu.toolintegrations.Entity.ResultEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:11
 **/

@Slf4j
public class ResultUtils {

    public static ResultEntity<Object> build(Object data) {
        ResultEntity<Object> resultEntity = new ResultEntity<>();
        resultEntity.setData(data);
        return resultEntity;
    }

}
