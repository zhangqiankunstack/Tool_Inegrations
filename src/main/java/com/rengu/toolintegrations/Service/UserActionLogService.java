package com.rengu.toolintegrations.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rengu.toolintegrations.Entity.UserActionLogEntity;
import com.rengu.toolintegrations.Repository.UserActionLogRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: Tool_integrations
 * @author: hanchangming
 * @create: 2018-08-22 17:07
 **/

@Slf4j
@Service
@Transactional
public class UserActionLogService {

    // 操作对象
    public static final int ERROR_OBJECT = 0;
    public static final int USER_OBJECT = 1;
    public static final int PROJECT_OBJECT = 2;
    public static final int DEVICE_OBJECT = 3;
    public static final int COMPONENT_OBJECT = 4;
    public static final int DEPLOYMENT_DESIGN_OBJECT = 5;

    // 操作类别
    public static final int ERROR_TYPE = 0;
    public static final int CREATE_TYPE = 1;
    public static final int DELETE_TYPE = 2;
    public static final int UPDATE_TYPE = 3;
    public static final int RESTORE_TYPE = 4;
    public static final int CLEAN_TYPE = 5;
    public static final int COPY_TYPE = 6;
    public static final int SCAN_TYPE = 7;
    public static final int EXPORT_TYPE = 7;

    private final UserActionLogRepository userActionLogRepository;

    @Autowired
    public UserActionLogService(UserActionLogRepository userActionLogRepository) {
        this.userActionLogRepository = userActionLogRepository;
    }

    // 保存用户操作日志
    public UserActionLogEntity saveUserActionLog(UserActionLogEntity userActionLogEntity) {
        log.info("已保存用户操作日志：" + userActionLogEntity.getDescription());
        return userActionLogRepository.save(userActionLogEntity);
    }

    // 根据用户名查询用户操作日志
    public Page<UserActionLogEntity> getUserActionLogsByUsername(Pageable pageable, String username) {
        return userActionLogRepository.findByUsername(pageable, username);
    }

    // 查询全部用户操作日志
    public Page<UserActionLogEntity> getUserActionLogs(Pageable pageable) {
        return userActionLogRepository.findAll(pageable);
    }

    //清除所有操作日志
    public void deletedUserActionLogById(String userActionLogId) {
        userActionLogRepository.deleteById(userActionLogId);
    }

    //根据id查询操作日志
    public UserActionLogEntity getUserActionLogById(String userActionLogId) {
        if (!hasUserActionLog(userActionLogId)) {
            throw new RuntimeException(ApplicationMessages.USERACTIONLOG_ID_NOT_FOUND + userActionLogId);
        }
        return userActionLogRepository.findById(userActionLogId).get();
    }

    //判断操作日志id是否存在
    private boolean hasUserActionLog(String userActionLogId) {
        if (StringUtils.isEmpty(userActionLogId)) {
            return false;
        }
        return userActionLogRepository.existsById(userActionLogId);
    }

    //导出操作日志信息至EXCEL表中
    public void exportUserAciotnLogByUserActionLog(String userActionLogId, String excelName) throws IOException {
        int nuwunm = 0;
        FileOutputStream fOut = null;
        //创建Excel表格式
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("操作日志");
        JSONArray jsonArrayList = JSONArray.parseArray(userActionLogId);
        for (Object obj : jsonArrayList) {
            JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            String userAcLoId = jsonObject.getString("id");
            UserActionLogEntity userActionLogEntity = getUserActionLogById(userAcLoId);
            String description = userActionLogEntity.getDescription();
            HSSFRow row = sheet.createRow(nuwunm);
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(description);
            //新建一输出文件流，把相应的Excel工作簿输出到本地
            fOut = new FileOutputStream(FileUtils.getTempDirectoryPath() + File.separator + excelName + ".xls");
            workbook.write(fOut);
            nuwunm++;
        }
        fOut.flush();
        fOut.close();
    }


    //创建表头(导出excel表格)
    private void createTitle(HSSFWorkbook workbook, HSSFSheet sheet) {
        HSSFRow row = sheet.createRow(0);
        //设置列宽，setColumnWidth的第二个参数要乘以256，这个参数的单位是1/256个字符宽度
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(3, 17 * 256);

        //设置为居中加粗
        HSSFCellStyle style = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        //style.setAlignment(HSSFCellStyle.);
        style.setFont(font);

        HSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("组件操作日志信息Excel表");
        cell.setCellStyle(style);

    }

    //导出数据生成Execel
    public File exportExcel(String userActionLogId, HttpServletResponse res) {
        int rowNum = 1;
        JSONArray jsonArrayList = JSONArray.parseArray(userActionLogId);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("操作日志");
        for (Object obj : jsonArrayList) {
            JSONObject jsonObject = JSONObject.parseObject(obj.toString());
            String userAcLoId = jsonObject.getString("id");
            UserActionLogEntity userActionLogEntity = getUserActionLogById(userAcLoId);
            String description = userActionLogEntity.getDescription();

            createTitle(workbook, sheet);
            //设置日期格式
            HSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

            //新增数据行，并且设置单元格数据
            HSSFRow row = sheet.createRow(rowNum);
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(description);

            rowNum++;
        }

        String fileName = "操作日志.xls";

        //生成excel文件
        if (
                buildExcelFile(fileName, workbook)
        ) {
            //浏览器下载excel
            if (res != null) {
                try {
                    OutputStream outputStream = res.getOutputStream();
                    res.setCharacterEncoding("utf-8");
                    res.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
                    res.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
                    //res.setContentType("application/vnd.ms-excel;charset=UTF-8");
                    workbook.write(outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    //通过userName批量导出操作日志
    public File exportExcelByName(String username, HttpServletResponse res) {
        int rowNum = 1;

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("操作日志");
        List<UserActionLogEntity> logs = userActionLogRepository.countByUsername(username)<=0?new ArrayList():userActionLogRepository.findAllByUsername(username);
        for (UserActionLogEntity obj : logs) {

            String description = obj.getDescription();

            createTitle(workbook, sheet);
            //设置日期格式
            HSSFCellStyle style = workbook.createCellStyle();
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));

            //新增数据行，并且设置单元格数据
            HSSFRow row = sheet.createRow(rowNum);
            HSSFCell cell = row.createCell((short) 0);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(description);

            rowNum++;
        }

        String fileName = "操作日志.xls";

        //生成excel文件
        if (
                buildExcelFile(fileName, workbook)
        ) {
            //浏览器下载excel
            if (res != null) {
                try {
                    OutputStream outputStream = res.getOutputStream();
                    res.setCharacterEncoding("utf-8");
                    res.setHeader("content-Type", "application/vnd.ms-excel;charset=UTF-8");
                    res.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
                    //res.setContentType("application/vnd.ms-excel;charset=UTF-8");
                    workbook.write(outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    //生成excel文件
    protected boolean buildExcelFile(String filepath, HSSFWorkbook workbook) {
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(filepath);
            workbook.write(fos);
            fos.flush();

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    //批量清除所有操作日志
    public void deletedUserActionLogByName(String username) {

        userActionLogRepository.deleteAllByUsername(username);
    }
}
