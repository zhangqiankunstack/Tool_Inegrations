package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import com.rengu.toolintegrations.Utils.CompressUtils;
import com.rengu.toolintegrations.Utils.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 17:27
 */

@Service
@Transactional

public class ToolFileService {
    private final ToolFileRepository toolFileRepository;
    private final FileService fileService;
    private final UserActionLogService userActionLogService;
    private final ToolService toolService;

    @Autowired
    public ToolFileService(ToolFileRepository toolFileRepository, FileService fileService, UserActionLogService userActionLogService, ToolService toolService) {
        this.toolFileRepository = toolFileRepository;
        this.fileService = fileService;
        this.userActionLogService = userActionLogService;
        this.toolService = toolService;
    }

    //根据工具类型或者文件名称组合模糊查询
    public List<ToolFileEntity> getToolFileFuzzQueryByToolNameOrByFileName(String toolName, String fileName, String userId, Pageable pageable) {
        //Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        return toolFileRepository.findByToolNameAndByFileNameAndByUserId(toolName, fileName, userId, pageable);
    }

    //根据用户查询所有工具文件
    public List<ToolFileEntity> getToolAllByUser(String userId, Pageable pageable) {
        return toolFileRepository.findByUserEntity(userId, pageable);
    }

    // 根据组件父节点保存文件
    public List<ToolFileEntity> saveToolFilesByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        List<ToolFileEntity> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (hasToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity)) {
                            ToolFileEntity toolFileEntity = getToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity);
                            toolFileEntity.setCreateTime(new Date());
                            toolFileEntity.setName(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolFileEntity.setFolder(false);
                            toolFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolFileEntityList.add(toolFileRepository.save(toolFileEntity));
                        } else {
                            ToolFileEntity toolFileEntity = new ToolFileEntity();
                            toolFileEntity.setName(StringUtils.isEmpty(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath())) ? "-" : FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolFileEntity.setFolder(false);
                            toolFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolFileEntity.setParentNode(parentNode);
                            toolFileEntity.setToolEntity(toolEntity);
                            toolFileEntityList.add(toolFileRepository.save(toolFileEntity));
                        }
                    } else {
                        // 路径节点，先判断是否存在该节点
                        if (hasToolFileByNameAndExtensionAndParentNodeAndTool(path, "?", parentNode, toolEntity)) {
                            parentNode = getToolFileByNameAndExtensionAndParentNodeAndTool(path, "?", parentNode, toolEntity);
                        } else {
                            ToolFileEntity toolFileEntity = new ToolFileEntity();
                            toolFileEntity.setName(path);
                            toolFileEntity.setExtension("?");
                            toolFileEntity.setFolder(true);
                            toolFileEntity.setParentNode(parentNode);
                            toolFileEntity.setToolEntity(toolEntity);
                            parentNode = toolFileRepository.save(toolFileEntity);
                            toolFileEntityList.add(toolFileEntity);
                        }
                    }
                }
            }
        }
        //生成历史版本很多，返回给我一次保存
        return toolFileEntityList;
    }

    //导出工具文件
    public File exportToolFileByTool(ToolEntity toolEntity, UserEntity userEntity) throws IOException {
        //todo:初始化导出目录
        File exportPath = new File(FileUtils.getTempDirectoryPath() + File.separator);
        //获取需要导出的工具文件（TODO：根据父类节点以及工具查询工具文件）
        for (ToolFileEntity toolFileEntity : getToolFileByparentNodeAndByTool(null, toolEntity)) {
            //导出工具文件
            exportToolFile(toolFileEntity, exportPath);
        }
        String username = userEntity.getUsername();
        String description = "用户：" + username + "导出组件：" + toolEntity.getName() + "文件"/*+ toolEntity.getStorageTime()+"导出次数"*/;
        UserActionLogEntity userActionLogEntity = new UserActionLogEntity();
        userActionLogEntity.setUsername(username);
        userActionLogEntity.setObject(UserActionLogService.USER_OBJECT);
        userActionLogEntity.setType(UserActionLogService.CREATE_TYPE);
        userActionLogEntity.setDescription(description);
        //TODO：导出次数修改
        //userActionLogEntity.setExportStatistics(toolEntity.getExportStatistics()+1);//导出统计次数

        userActionLogService.saveUserActionLog(userActionLogEntity);

        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName() + ".zip"));
    }

    //根据父类节点以及工具查询工具文件
    public List<ToolFileEntity> getToolFileByparentNodeAndByTool(String parentNodeId, ToolEntity toolEntity) {
        //判断父类节点是否存在
        ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
        return toolFileRepository.findByParentNodeAndToolEntity(parentNode, toolEntity);
    }

    //导出工具文件
    public File exportToolFile(ToolFileEntity toolFileEntity, File exportPath) throws IOException {
        ToolEntity toolEntity = toolFileEntity.getToolEntity();
        //遍历是文件还是文件夹
        if (toolFileEntity.isFolder()) {
            for (ToolFileEntity fileEntity : getToolFileByparentNodeAndByTool(toolFileEntity.getId(), toolEntity)) {
                exportToolFile(fileEntity, exportPath);
            }
        } else {
            File file = new File(exportPath.getAbsolutePath() + File.separator + FormatUtils.getToolFileRelativePath(toolFileEntity, ""));
            //todo:copyFile导包可能有问题
            FileUtils.copyFile(new File(toolFileEntity.getFileEntity().getLocalPath()), file);
        }
        //TODO :不知道为什么要返回exportPath
        return exportPath;
    }

    // 根据名称、父节点及组件检查文件是否存在
    public boolean hasToolFileByNameAndExtensionAndParentNodeAndTool(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(extension)) {
            return false;
        }
        return toolFileRepository.existsByNameAndExtensionAndParentNodeAndToolEntity(name, extension, parentNode, toolEntity);
    }

    // 根据名称、父节点及组件查询文件
    public ToolFileEntity getToolFileByNameAndExtensionAndParentNodeAndTool(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity) {
        return toolFileRepository.findByNameAndExtensionAndParentNodeAndToolEntity(name, extension, parentNode, toolEntity).get();
    }

    // 根据id查询组件文件
    public ToolFileEntity getToolFileById(String toolFileId) {
        if (!hasToolFileById(toolFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_FILE_ID_NOT_FOUND + toolFileId);
        }
        return toolFileRepository.findById(toolFileId).get();
    }

    // 根据Id查询工具文件是否存在
    public boolean hasToolFileById(String toolFileId) {
        if (StringUtils.isEmpty(toolFileId)) {
            return false;
        }
        return toolFileRepository.existsById(toolFileId);
    }

    //根据工具id查询工具文件
    public List<ToolFileEntity> getToolFilesByToolId(String toolId, Pageable pageable) {
        boolean bool = toolService.hasToolById(toolId);
        if (!bool == true) {
            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND + toolId);
        }
        List<ToolFileEntity> toolFileEntityList = toolFileRepository.findByToolId(toolId, pageable);
        return toolFileEntityList;
    }
}
