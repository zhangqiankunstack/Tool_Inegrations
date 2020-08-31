package com.rengu.toolintegrations.Utils;

import com.rengu.toolintegrations.Entity.ToolAchievementsFile;
import com.rengu.toolintegrations.Entity.ToolFileEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-24 13:08
 **/

@Slf4j
public class FormatUtils {

    /**
     * 格式化文件路径，将其中不规范的分隔转换为标准的分隔符,并且去掉末尾的"/"符号。
     *
     * @param path 文件路径
     * @return 格式化后的文件路径
     */
    public static String formatPath(String path) {
        String reg0 = "\\\\＋";
        String reg = "\\\\＋|/＋";
        String temp = path.trim().replaceAll(reg0, "/");
        temp = temp.replaceAll(reg, "/");
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        if (System.getProperty("file.separator").equals("\\")) {
            temp = temp.replace('/', '\\');
        }
        return FilenameUtils.separatorsToUnix(temp);
    }

    // 生成指定长度的字符串
    public static String getString(String string, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        stringBuilder.setLength(length);
        return stringBuilder.toString();
    }

    // 从字符串生成指定长度的字节数组
    public static byte[] getBytesFormString(String string, int length) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(string.getBytes());
        return byteBuffer.array();
    }

//
    // 递归拼接path信息
    public static String getToolFileRelativePath(ToolFileEntity toolFileEntity, String basePath) {
        if (basePath.isEmpty()) {
            if (toolFileEntity.isFolder()) {
                basePath = File.separatorChar + toolFileEntity.getName() + File.separatorChar;
                System.out.println("是文件夹的情况下获取了什么内容："+basePath);
            } else {
                basePath = StringUtils.isEmpty(toolFileEntity.getFileEntity().getType()) ? File.separatorChar + toolFileEntity.getName() : File.separatorChar + toolFileEntity.getName() + "." + toolFileEntity.getFileEntity().getType();
                System.out.println("不是文件夹的情况下获取了什么路径："+basePath);
            }
        }
        while (toolFileEntity.getParentNode() != null) {
            toolFileEntity = toolFileEntity.getParentNode();
            basePath = File.separatorChar + toolFileEntity.getName() + basePath;
            getToolFileRelativePath(toolFileEntity, basePath);
        }
        return FormatUtils.formatPath(basePath);
    }

    // 递归拼接path信息
    public static String getComponentFileRelativePath(ToolAchievementsFile toolAchievementsFile, String basePath) {
        if (basePath.isEmpty()) {
            if (toolAchievementsFile.isFolder()) {
                basePath = File.separatorChar + toolAchievementsFile.getName() + File.separatorChar;
            } else {
                basePath = StringUtils.isEmpty(toolAchievementsFile.getFileEntity().getType()) ? File.separatorChar + toolAchievementsFile.getName() : File.separatorChar + toolAchievementsFile.getName() + "." + toolAchievementsFile.getFileEntity().getType();
            }
        }
        while (toolAchievementsFile.getParentNode() != null) {
            toolAchievementsFile = toolAchievementsFile.getParentNode();
            basePath = File.separatorChar + toolAchievementsFile.getName() + basePath;
            getComponentFileRelativePath(toolAchievementsFile, basePath);
        }
        return FormatUtils.formatPath(basePath);
    }

    // 递归拼接path信息
//    public static String getComponentFileHistoryRelativePath(ComponentFileHistoryEntity componentFileHistoryEntity, String basePath) {
//        if (basePath.isEmpty()) {
//            if (componentFileHistoryEntity.isFolder()) {
//                basePath = File.separatorChar + componentFileHistoryEntity.getName() + File.separatorChar;
//            } else {
//                basePath = StringUtils.isEmpty(componentFileHistoryEntity.getFileEntity().getType()) ? File.separatorChar + componentFileHistoryEntity.getName() : File.separatorChar + componentFileHistoryEntity.getName() + "." + componentFileHistoryEntity.getFileEntity().getType();
//            }
//        }
//        while (componentFileHistoryEntity.getParentNode() != null) {
//            componentFileHistoryEntity = componentFileHistoryEntity.getParentNode();
//            basePath = File.separatorChar + componentFileHistoryEntity.getName() + basePath;
//            getComponentFileHistoryRelativePath(componentFileHistoryEntity, basePath);
//        }
//        return FormatUtils.formatPath(basePath);
//    }
}
