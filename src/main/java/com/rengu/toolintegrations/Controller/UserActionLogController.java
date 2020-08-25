package com.rengu.toolintegrations.Controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rengu.toolintegrations.Entity.ResultEntity;
import com.rengu.toolintegrations.Service.UserActionLogService;
import com.rengu.toolintegrations.Utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:14
 **/


@RestController
@RequestMapping(value = "/useractionlogs")
public class UserActionLogController {

    private final UserActionLogService userActionLogService;

    @Autowired
    public UserActionLogController(UserActionLogService userActionLogService) {
        this.userActionLogService = userActionLogService;
    }

    // 查询全部用户操作日志
    @GetMapping
    public ResultEntity getUserActionLogs(@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResultUtils.build(userActionLogService.getUserActionLogs(pageable));
    }

    //批量清空操作日志
    @DeleteMapping(value = "/deleteAll")
    public void cleatUserAciotnLog(@RequestBody @Valid String userActionLogIds) {
        JSONArray jsonArrayList = JSONArray.parseArray(userActionLogIds);
        for (Object obj : jsonArrayList) {
            JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            String userAcLoId = jsonObject.getString("id");
            userActionLogService.deletedUserActionLogById(userAcLoId);
        }
    }

    //批量清空操作日志
    @DeleteMapping(value = "/deleteAllByName")
    public void cleatUserAciotnLogByName(@RequestBody @Valid String username) {

            userActionLogService.deletedUserActionLogByName(username);

    }

    //导出操作日志（测试版）
    @GetMapping(value = "/exportExcel")
    public void exportUserAciotnLog(@RequestBody @Valid String userActionLogId, String excelName) throws IOException {
        userActionLogService.exportUserAciotnLogByUserActionLog(userActionLogId,excelName);
    }

    //导出操作日志数据（Excel）
    @PostMapping("/export")
    public File export2Excel(@RequestBody @Valid String userActionLogId, HttpServletResponse res) {
        return userActionLogService.exportExcel(userActionLogId, res);
    }

    @PostMapping("/exportByName")
    public File export2ExcelByName(@RequestBody @Valid String username, HttpServletResponse res) {
        return userActionLogService.exportExcelByName(username, res);
    }
}
