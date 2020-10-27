package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolEnvironmentFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import com.rengu.toolintegrations.Utils.CompressUtils;
import com.rengu.toolintegrations.Utils.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName ToolEnvironmentFileService
 * @Description 工具环境业务逻辑层
 * @Author yyc
 * @Date 2020/8/26 13:56
 * @Version 1.0
 */
@Service
public class ToolEnvironmentFileService {
    private final ToolEnvironmentFileRepository toolEnvironmentFileRepository;
    private final FileService fileService;
    private final UserActionLogService userActionLogService;
    private final ToolConsequenceFileService toolConsequenceFileService;

    public ToolEnvironmentFileService(FileService fileService, ToolEnvironmentFileRepository toolEnvironmentFileRepository, UserActionLogService userActionLogService, ToolConsequenceFileService toolConsequenceFileService) {
        this.toolEnvironmentFileRepository = toolEnvironmentFileRepository;
        this.fileService = fileService;
        this.userActionLogService = userActionLogService;
        this.toolConsequenceFileService = toolConsequenceFileService;
    }

    //根据工具清除工具环境文件
    public List<ToolEnvironmentFileEntity> cleanToolConsequenceFileByToolEntity(ToolEntity toolEntity) throws IOException {
        List<ToolEnvironmentFileEntity> toolEnvironmentFileEntityList = getToolEnvironmentFilesByParentNodeAndToolEntity(null, toolEntity);
        for (ToolEnvironmentFileEntity toolEnvironmentFileEntity : toolEnvironmentFileEntityList) {
            deleteToolEnvironmentFileByToolEnvironmentFile(toolEnvironmentFileEntity);
        }
        return toolEnvironmentFileEntityList;
    }

    public ToolEnvironmentFileEntity deleteToolEnvironmentFileByToolEnvironmentFile(ToolEnvironmentFileEntity toolEnvironmentFileEntity) throws IOException {
        if (toolEnvironmentFileEntity.isFolder()) {
            for (ToolEnvironmentFileEntity tempEnvironmentFile : getToolEnvironmentFilesByParentNodeAndToolEntity(toolEnvironmentFileEntity.getId(), toolEnvironmentFileEntity.getToolEntity())) {
                deleteToolEnvironmentFileByToolEnvironmentFile(tempEnvironmentFile);
            }
            toolEnvironmentFileRepository.deleteById(toolEnvironmentFileEntity.getId());
        } else {
            toolEnvironmentFileRepository.deleteById(toolEnvironmentFileEntity.getId());
            if (!hasToolEnvironmentFileByFile(toolEnvironmentFileEntity.getFileEntity()) && !toolConsequenceFileService.hasToolConsequenceFileByFile(toolEnvironmentFileEntity.getFileEntity())) {
                fileService.deleteFileById(toolEnvironmentFileEntity.getFileEntity().getId());
            }
        }
        return toolEnvironmentFileEntity;
    }

    // 根据父节点保存文件
    public List<ToolEnvironmentFileEntity> saveToolAchievementsFileByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        List<ToolEnvironmentFileEntity> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolEnvironmentFileEntity parentNode = hasToolEnvironmentFileById(parentNodeId) ? getToolEnvironmentFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolEntity(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity)) {
                            ToolEnvironmentFileEntity toolEnvironmentFileEntity = getToolFileByNameAndExtensionAndToolEntity(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity);
                            toolEnvironmentFileEntity.setName(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolEnvironmentFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolEnvironmentFileEntity.setFolder(false);
                            toolEnvironmentFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolFileEntityList.add(toolEnvironmentFileRepository.save(toolEnvironmentFileEntity));
                        } else {
                            ToolEnvironmentFileEntity toolEnvironmentFileEntity = new ToolEnvironmentFileEntity();
                            toolEnvironmentFileEntity.setName(StringUtils.isEmpty(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath())) ? "-" : FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolEnvironmentFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolEnvironmentFileEntity.setFolder(false);
                            toolEnvironmentFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolEnvironmentFileEntity.setParentNode(parentNode);
                            toolEnvironmentFileEntity.setToolEntity(toolEntity);
                            toolFileEntityList.add(toolEnvironmentFileRepository.save(toolEnvironmentFileEntity));
                        }
                    } else {
                        // 路径节点，先判断是否存在该节点
                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolEntity(path, "?", parentNode, toolEntity)) {
                            parentNode = getToolFileByNameAndExtensionAndToolEntity(path, "?", parentNode, toolEntity);
                        } else {
                            ToolEnvironmentFileEntity toolEnvironmentFileEntity = new ToolEnvironmentFileEntity();
                            toolEnvironmentFileEntity.setName(path);
                            toolEnvironmentFileEntity.setExtension("?");
                            toolEnvironmentFileEntity.setFolder(true);
                            toolEnvironmentFileEntity.setParentNode(parentNode);
                            toolEnvironmentFileEntity.setToolEntity(toolEntity);
                            parentNode = toolEnvironmentFileRepository.save(toolEnvironmentFileEntity);
                            toolFileEntityList.add(toolEnvironmentFileEntity);
                        }
                    }
                }
            }
        }
        return toolFileEntityList;
    }

    public ToolEnvironmentFileEntity getToolEnvironmentFileById(String toolEnvironmentFileId) {
        if (!hasToolEnvironmentFileById(toolEnvironmentFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_ENVIRONMENT_FILE_NAME_ID_NOT_FOUND);
        }
        return toolEnvironmentFileRepository.findById(toolEnvironmentFileId).get();
    }

    //导出工具环境文件
    public File exportEnvironmentFileById(String achievementsFilesId) throws IOException {
        ToolEnvironmentFileEntity toolEnvironmentFileEntity = getToolEnvironmentFileById(achievementsFilesId);
        if (toolEnvironmentFileEntity.isFolder()) {
            //初始化导出目录
            File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separator + UUID.randomUUID().toString());
            exportDir.mkdirs();
            exportEnvironmentFile(toolEnvironmentFileEntity, exportDir);
            return CompressUtils.compress(exportDir, new File(FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis() + ".zip"));
        } else {
            File exportFile = new File(FileUtils.getTempDirectoryPath() + File.separator + toolEnvironmentFileEntity.getName() + "." + toolEnvironmentFileEntity.getFileEntity().getType());
            FileUtils.copyFile(new File(toolEnvironmentFileEntity.getFileEntity().getLocalPath()), exportFile);
            return exportFile;
        }
    }

    //导出环境文件
    public File exportEnvironmentFile(ToolEnvironmentFileEntity toolEnvironmentFileEntity, File exportDir) throws IOException {
        if (toolEnvironmentFileEntity.isFolder()) {
            for (ToolEnvironmentFileEntity toolEnvironmentFileEntity1 : getToolEnvironmentFilesByParentNodeAndToolEntity(toolEnvironmentFileEntity.getId(), toolEnvironmentFileEntity.getToolEntity())) {
                exportEnvironmentFile(toolEnvironmentFileEntity1, exportDir);
            }
        } else {
            File file = new File(exportDir.getAbsolutePath() + File.separator + FormatUtils.getEnvironmentFileRelativePath(toolEnvironmentFileEntity, ""));
            FileUtils.copyFile(new File(toolEnvironmentFileEntity.getFileEntity().getLocalPath()), file);
        }
        return exportDir;
    }

    // 查询父节点和组件查询环境文件
    public List<ToolEnvironmentFileEntity> getToolEnvironmentFilesByParentNodeAndToolEntity(String parentNodeId, ToolEntity toolEntity) {
        ToolEnvironmentFileEntity parentNode = hasToolEnvironmentFileById(parentNodeId) ? getToolEnvironmentFileById(parentNodeId) : null;
        return toolEnvironmentFileRepository.findByParentNodeAndToolEntity(parentNode, toolEntity);
    }

//    //TODO：查看环境文件9月21日
//    public List<ToolFileEntity> findToolEnvironmentFilesByToolAchievementsId(String toolId, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) {
//        if (StringUtils.isEmpty(toolId)) {
//            throw new RuntimeException(ApplicationMessages.TOOL_ENVIRONMENT_ID_NOT_FOUND);
//        }
//        return toolFileService.getToolFilesByToolId(toolId, pageable);
//    }

    // 根据Id查询工具环境文件是否存在
    public boolean hasToolEnvironmentFileById(String toolEnvironmentFileId) {
        if (StringUtils.isEmpty(toolEnvironmentFileId)) {
            return false;
        }
        return toolEnvironmentFileRepository.existsById(toolEnvironmentFileId);
    }

    public boolean hasToolAchievementsByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolEnvironmentFileEntity parentNode, ToolEntity toolEntity) {
        if (!StringUtils.isEmpty(name) || !StringUtils.isEmpty(extension)) {
            return false;
        }
        return toolEnvironmentFileRepository.existsByNameAndExtensionAndParentNodeAndToolEntity(name, extension, parentNode, toolEntity);
    }

    // 根据名称、父节点及工具查询文件
    public ToolEnvironmentFileEntity getToolFileByNameAndExtensionAndToolEntity(String name, String extension, ToolEnvironmentFileEntity parentNode, ToolEntity toolEntity) {
        return toolEnvironmentFileRepository.findByNameAndExtensionAndParentNodeAndToolEntity(name, extension, parentNode, toolEntity).get();
    }

    public List<ToolEnvironmentFileEntity> getToolEnvironmentFileAllByUser(String userId) {
        return toolEnvironmentFileRepository.findByUserId(userId);
    }

    public boolean hasToolEnvironmentFileByFile(FileEntity fileEntity) {
        return toolEnvironmentFileRepository.existsByFileEntity(fileEntity);
    }

    //通过工具id导出环境文件
    public File exportToolEnvironmentByTool(ToolEntity toolEntity, UserEntity userEntity) throws IOException {
        //todo:初始化导出目录
        File exportPath = new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName());
        exportPath.mkdirs();
        //导出环境文件
        for (ToolEnvironmentFileEntity toolEnvironmentFileEntity : getToolEnvironmentFilesByParentNodeAndToolEntity(null, toolEntity)) {
            exportEnvironmentFile(toolEnvironmentFileEntity, exportPath);
        }
        String username = userEntity.getUsername();
        String description = "用户：" + username + "导出工具：" + toolEntity.getName();
        UserActionLogEntity userActionLogEntity = new UserActionLogEntity();
        userActionLogEntity.setUsername(username);
        userActionLogEntity.setObject(UserActionLogService.USER_OBJECT);
        userActionLogEntity.setType(UserActionLogService.CREATE_TYPE);
        userActionLogEntity.setDescription(description);
        userActionLogService.saveUserActionLog(userActionLogEntity);
//        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName() + ".zip"));
        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + "环境文件" + ".zip"));
    }
}