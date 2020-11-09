package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolFileAndUserRepository;
import com.rengu.toolintegrations.Repository.ToolFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import com.rengu.toolintegrations.Utils.CompressUtils;
import com.rengu.toolintegrations.Utils.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.omg.CORBA.SystemException;
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
    private final ToolFileAndUserService toolFileAndUserService;
    private final ToolFileAndUserRepository toolFileAndUserRepository;
    private final ToolConsequenceFileService toolConsequenceFileService;
    private final ToolEnvironmentFileService toolEnvironmentFileService;

    @Autowired
    public ToolFileService(ToolFileRepository toolFileRepository, FileService fileService, UserActionLogService userActionLogService, ToolFileAndUserService toolFileAndUserService, ToolFileAndUserRepository toolFileAndUserRepository, ToolConsequenceFileService toolConsequenceFileService, ToolEnvironmentFileService toolEnvironmentFileService) {
        this.toolFileRepository = toolFileRepository;
        this.fileService = fileService;
        this.userActionLogService = userActionLogService;
        this.toolFileAndUserService = toolFileAndUserService;
        this.toolFileAndUserRepository = toolFileAndUserRepository;
        this.toolConsequenceFileService = toolConsequenceFileService;
        this.toolEnvironmentFileService = toolEnvironmentFileService;
    }

//    //根据工具类型或者文件名称组合模糊查询
//    public List<ToolFileEntity> getToolFileFuzzQueryByToolTypeOrByFileName(String toolType, String fileName, String userId, Pageable pageable) {
//        //Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
//        return toolFileRepository.findBytoolTypeAndByFileNameAndByUserId(toolType, fileName, userId, pageable);
//    }

    //todo 查询工具数量，暂时不用
//    public List<ToolFileEntity> getToolAllByUser(String userId) {
//        return toolFileRepository.findByUserId(userId);
//    }

    //根据工具删除工具文件
    public List<ToolFileEntity> cleanToolFileByToolEntity(ToolEntity toolEntity) throws IOException {
        List<ToolFileEntity> toolFileList = getToolFilesByParentNodeAndTool(null, toolEntity);
        for (ToolFileEntity toolFile : toolFileList) {
            //TODO：删除用户下载次数表信息
//            toolFileAndUserRepository.deleteByToolId(toolFile.getId());
            deleteToolFile(toolFile);
        }
        return toolFileList;
    }

    public ToolFileEntity deleteToolFile(ToolFileEntity toolFileEntity) throws IOException {
        if (toolFileEntity.isFolder()) {
            for (ToolFileEntity tempToolFile : getToolFilesByParentNodeAndTool(toolFileEntity.getId(), toolFileEntity.getToolEntity())) {
                deleteToolFile(tempToolFile);
            }
            toolFileRepository.deleteById(toolFileEntity.getId());
        } else {
            toolFileRepository.deleteById(toolFileEntity.getId());
            if (!hasToolFileByFile(toolFileEntity.getFileEntity()) && !toolConsequenceFileService.hasToolConsequenceFileByFile(toolFileEntity.getFileEntity()) && !toolEnvironmentFileService.hasToolEnvironmentFileByFile(toolFileEntity.getFileEntity())) {
                fileService.deleteFileById(toolFileEntity.getFileEntity().getId());
            }
        }
        return toolFileEntity;
    }

    //根据功能删除工具文件
    public ToolFileEntity deleteToolFileById(String toolId) throws IOException {
        ToolFileEntity toolFileEntity = getToolFileById(toolId);
        deleteToolFile(toolFileEntity);
        return toolFileEntity;
    }

    // 根据组件父节点创建文件夹
    public void saveToolFileByTool(ToolEntity toolEntity, String parentNodeId) {
        ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
        String types = "执行程序,依赖环境,文档资料,安装程序";
        String[] type = types.split(",");
        for (int i = 0; i < type.length; i++) {
            ToolFileEntity toolFileEntity = new ToolFileEntity();
            toolFileEntity.setName(type[i]);
            toolFileEntity.setExtension("?");
            toolFileEntity.setFolder(true);
            toolFileEntity.setParentNode(parentNode);
            toolFileEntity.setToolEntity(toolEntity);
            toolFileRepository.save(toolFileEntity);
        }
    }

    //根据成果文件父节点创建文件夹
    public ToolFileEntity saveToolFileByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, ToolFileEntity toolFileEntity) {
        ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
        if (StringUtils.isEmpty(toolFileEntity.getName())) {
            throw new RuntimeException(ApplicationMessages.TOOL_FILE_NAME_ARGS_NOT_FOUND);
        }
        toolFileEntity.setName(getName(toolFileEntity.getName(), toolFileEntity.getExtension(), parentNode, toolEntity));
        toolFileEntity.setExtension("?");
        toolFileEntity.setFolder(true);
        toolFileEntity.setParentNode(parentNode);
        toolFileEntity.setToolEntity(toolEntity);
        return toolFileRepository.save(toolFileEntity);
    }

    //获取不重复的文件/文件夹名
    public String getName(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity) {
        int index = 0;
        while (hasToolFileByNameAndExtensionAndParentNodeAndTool(name, extension, parentNode, toolEntity)) {
            index = index + 1;
            name = name + "(" + index + ")";
        }
        return name;
    }

    // 根据组件父节点保存文件
    public List<ToolFileEntity> saveToolFilesByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        List<ToolFileEntity> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            System.out.println("分割出来的：" + splitPaths);
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                System.out.println("获取的什么参数：" + path);
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (hasToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity)) {
                            ToolFileEntity toolFileEntity = getToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity);
                            System.out.println("获取了什么值：" + toolFileEntity);
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
        return toolFileEntityList;
    }


    //导出工具文件以及环境文件
    public File exportToolFileByTool(ToolEntity toolEntity, UserEntity userEntity) throws IOException {
        //todo:初始化导出目录
        File exportPath = new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName());
        exportPath.mkdirs();
        //导出工具文件
        for (ToolFileEntity toolFileEntity : getToolFilesByParentNodeAndTool(null, toolEntity)) {
            exportToolFile(toolFileEntity, exportPath);
        }
        //导出环境文件
        for (ToolEnvironmentFileEntity toolEnvironmentFileEntity : toolEnvironmentFileService.getToolEnvironmentFilesByParentNodeAndToolEntity(null, toolEntity)) {
            toolEnvironmentFileService.exportEnvironmentFile(toolEnvironmentFileEntity, exportPath);
        }
        String username = userEntity.getUsername();
        String description = "用户：" + username + "导出工具：" + toolEntity.getName();
        UserActionLogEntity userActionLogEntity = new UserActionLogEntity();
        userActionLogEntity.setUsername(username);
        userActionLogEntity.setObject(UserActionLogService.USER_OBJECT);
        userActionLogEntity.setType(UserActionLogService.CREATE_TYPE);
        userActionLogEntity.setDescription(description);
        userActionLogService.saveUserActionLog(userActionLogEntity);
        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName() + ".zip"));
    }

    //导出工具文件
    public File exportToolFile(ToolFileEntity toolFileEntity, File exportPath) throws IOException {
        ToolEntity toolEntity = toolFileEntity.getToolEntity();
        if (toolFileEntity.isFolder()) {
            for (ToolFileEntity fileEntity : getToolFilesByParentNodeAndTool(toolFileEntity.getId(), toolEntity)) {
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

    //根据名称、父节点及组件检查文件是否存在
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

    public ToolFileEntity getToolFileById(String toolFileId) {
        if (!hasToolFileById(toolFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_FILE_ID_NOT_FOUND + toolFileId);
        }
        return toolFileRepository.findById(toolFileId).get();
    }

    public boolean hasToolFileById(String toolFileId) {
        if (StringUtils.isEmpty(toolFileId)) {
            return false;
        }
        return toolFileRepository.existsById(toolFileId);
    }

    //根据工具id查询工具文件
    public List<ToolFileEntity> getToolFilesByToolId(String toolId, Pageable pageable) {
//        boolean bool = toolService.hasToolById(toolId);
//        if (!bool == true) {
//            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND + toolId);
//        }
        List<ToolFileEntity> toolFileEntityList = toolFileRepository.findByToolId(toolId, pageable);
        return toolFileEntityList;
    }

    // 根据Id导出组件文件
    public File exportToolFileById(String toolFileId, String userId) throws IOException {
        /*toolFileAndUserService.saveToolFileAndUser(toolFileId, userId);*/
        ToolFileEntity toolFileEntity = getToolFileById(toolFileId);
        if (toolFileEntity.isFolder()) {
            // 初始化导出目录
            ToolEntity toolEntity = toolFileEntity.getToolEntity();
            File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName());
            exportDir.mkdirs();
            exportToolFiles(toolFileEntity, exportDir);
            return CompressUtils.compress(exportDir, new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName() + ".zip"));
        } else {
            File exportFile = new File(FileUtils.getTempDirectoryPath() + File.separator + toolFileEntity.getName() + "." + toolFileEntity.getFileEntity().getType());

            FileUtils.copyFile(new File(toolFileEntity.getFileEntity().getLocalPath()), exportFile);
            return exportFile;
        }
    }

    // 导出工具文件
    public File exportToolFiles(ToolFileEntity toolFileEntity, File exportDir) throws IOException {
        // 检查是否为文件夹
        ToolEntity toolEntity = toolFileEntity.getToolEntity();
        if (toolFileEntity.isFolder()) {
            for (ToolFileEntity tempToolFile : getToolFilesByParentNodeAndTool(toolFileEntity.getId(), toolEntity)) {
                exportToolFiles(tempToolFile, exportDir);
            }
        } else {
            File file = new File(exportDir.getAbsolutePath() + File.separator + FormatUtils.getToolFileRelativePath(toolFileEntity, ""));
            FileUtils.copyFile(new File(toolFileEntity.getFileEntity().getLocalPath()), file);
        }
        return exportDir;
    }

    // 根据父节点和工具查询工具文件（select * from component_file_entity  where parent_node_id = '1823bc7c-23ef-40b9-b5da-8e5a4e17bd66' AND component_entity_id = 'c34980e9-a93f-425b-b4ef-41f0f3bcc26d';）
    public List<ToolFileEntity> getToolFilesByParentNodeAndTool(String parentNodeId, ToolEntity toolEntity) {
        ToolFileEntity parentNode = hasToolFileById(parentNodeId) ? getToolFileById(parentNodeId) : null;
        //TODO:select * from component_file_entity  where parent_node_id = '1823bc7c-23ef-40b9-b5da-8e5a4e17bd66' AND component_entity_id = 'c34980e9-a93f-425b-b4ef-41f0f3bcc26d';
        List<ToolFileEntity> list = toolFileRepository.findByParentNodeAndToolEntity(parentNode, toolEntity);
        return list;
    }

    //根据引用文件判断是否存在
    public boolean hasToolFileByFile(FileEntity fileEntity) {
        return toolFileRepository.existsByFileEntity(fileEntity);
    }

    //导出工具文件
    public File exportToolFileByToolEntity(ToolEntity toolEntity, UserEntity userEntity) throws IOException {
        //todo:初始化导出目录
        File exportPath = new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName());
        exportPath.mkdirs();
        //导出工具文件
        for (ToolFileEntity toolFileEntity : getToolFilesByParentNodeAndTool(null, toolEntity)) {
            exportToolFile(toolFileEntity, exportPath);
        }
        String username = userEntity.getUsername();
        String description = "用户：" + username + "、导出工具：" + toolEntity.getName();
        UserActionLogEntity userActionLogEntity = new UserActionLogEntity();
        userActionLogEntity.setUsername(username);
        userActionLogEntity.setObject(UserActionLogService.USER_OBJECT);
        userActionLogEntity.setType(UserActionLogService.CREATE_TYPE);
        userActionLogEntity.setDescription(description);
        userActionLogService.saveUserActionLog(userActionLogEntity);
        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + toolEntity.getName() + ".zip"));
    }
}
