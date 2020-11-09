package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolConsequenceFileRepository;
import com.rengu.toolintegrations.Repository.ToolConsequenceRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import com.rengu.toolintegrations.Utils.CompressUtils;
import com.rengu.toolintegrations.Utils.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: Zhangqiankun
 * Date: 2020/9/3 13:39
 */
@Service
public class ToolConsequenceFileService {
    private final ToolConsequenceFileRepository toolConsequenceFileRepository;
    private final FileService fileService;
    private final UserService userService;
    private final ToolConsequenceRepository toolConsequenceRepository;
    private final UserActionLogService userActionLogService;

    @Autowired
    public ToolConsequenceFileService(ToolConsequenceFileRepository toolConsequenceFileRepository, FileService fileService, UserService userService, ToolConsequenceRepository toolConsequenceRepository, UserActionLogService userActionLogService) {
        this.toolConsequenceFileRepository = toolConsequenceFileRepository;
        this.fileService = fileService;
        this.userService = userService;
        this.toolConsequenceRepository = toolConsequenceRepository;
        this.userActionLogService = userActionLogService;
    }

    //通过成果清除成果文件
    public List<ToolConsequenceFileEntity> deletedToolConsequenceFileByToolConsequence(ToolConsequenceEntity toolConsequenceEntity) throws IOException {
        List<ToolConsequenceFileEntity> componentFileEntityList = getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceEntity, null);
        for (ToolConsequenceFileEntity toolConsequenceFileEntity : componentFileEntityList) {
            deleteComponentFile(toolConsequenceFileEntity);
        }
        return componentFileEntityList;
    }

    public ToolConsequenceFileEntity deleteComponentFile(ToolConsequenceFileEntity toolConsequenceFileAgrs) throws IOException {
        if (toolConsequenceFileAgrs.isFolder()) {
            for (ToolConsequenceFileEntity toolConsequenceFileEntity : getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceFileAgrs.getToolConsequenceEntity(), toolConsequenceFileAgrs.getId())) {
                deleteComponentFile(toolConsequenceFileEntity);
            }
//            toolConsequenceRepository.deleteById(toolConsequenceFileAgrs.getId());
            toolConsequenceFileRepository.deleteById(toolConsequenceFileAgrs.getId());
        } else {
//            toolConsequenceRepository.deleteById(toolConsequenceFileAgrs.getId());
            toolConsequenceFileRepository.deleteById(toolConsequenceFileAgrs.getId());
            if (!hasToolConsequenceFileByFile(toolConsequenceFileAgrs.getFileEntity())) {
                fileService.deleteFileById(toolConsequenceFileAgrs.getFileEntity().getId());
            }
        }
        return toolConsequenceFileAgrs;
    }

    //根据id删除成果文件
    public ToolConsequenceFileEntity deletedToolConsequenceFileById(String toolConsequenceFileId) throws IOException {
        ToolConsequenceFileEntity toolConsequenceFileEntity = getToolConsequenceFileById(toolConsequenceFileId);
        deletedToolConsequenceFile(toolConsequenceFileEntity);
        return toolConsequenceFileEntity;
    }

    public ToolConsequenceFileEntity deletedToolConsequenceFile(ToolConsequenceFileEntity toolConsequenceFileEntity) throws IOException {
        if (toolConsequenceFileEntity.isFolder()) {
            for (ToolConsequenceFileEntity tempConsequenceFile : getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceFileEntity.getToolConsequenceEntity(), toolConsequenceFileEntity.getId())) {
                deletedToolConsequenceFile(tempConsequenceFile);
            }
            toolConsequenceFileRepository.deleteById(toolConsequenceFileEntity.getId());
        } else {
            toolConsequenceFileRepository.deleteById(toolConsequenceFileEntity.getId());
            if (!hasToolConsequenceFileByFile(toolConsequenceFileEntity.getFileEntity())) {
                fileService.deleteFileById(toolConsequenceFileEntity.getFileEntity().getId());
            }
        }
        return toolConsequenceFileEntity;
    }

    //通过文件判断工具成果文件是否存在
    public boolean hasToolConsequenceFileByFile(FileEntity fileEntity) {
        return toolConsequenceFileRepository.existsByFileEntity(fileEntity);
    }

    //根据成果父节点创建文件夹



    // 根据组件父节点保存文件
    public List<ToolConsequenceFileEntity> saveToolFilesByParentNodeAndTool(String toolConsequenceName, String userId, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        ToolConsequenceEntity toolConsequenceAgrs = new ToolConsequenceEntity();
        toolConsequenceAgrs.setName(toolConsequenceName);
        toolConsequenceAgrs.setUserEntity(userService.getUserById(userId));
        toolConsequenceRepository.save(toolConsequenceAgrs);
        List<ToolConsequenceFileEntity> toolConsequenceFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolConsequenceFileEntity parentNode = hasToolConsequenceFileById(parentNodeId) ? getToolConsequenceFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            System.out.println("分割出来的：" + splitPaths);
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                System.out.println("获取的什么参数：" + path);
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (hasToolConsequenceFileByNameAndExtensionAndParentNodeAndToolConsequence(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolConsequenceAgrs)) {
                            ToolConsequenceFileEntity toolConsequenceFileEntity = getToolConsequenceFileByNameAndExtensionAndToolConsequenceEntity(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolConsequenceAgrs);
                            System.out.println("获取了什么值：" + toolConsequenceFileEntity);
                            toolConsequenceFileEntity.setCreateTime(new Date());
                            toolConsequenceFileEntity.setName(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolConsequenceFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolConsequenceFileEntity.setFolder(false);
                            toolConsequenceFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolConsequenceFileEntityList.add(toolConsequenceFileRepository.save(toolConsequenceFileEntity));
                        } else {
                            ToolConsequenceFileEntity toolConsequenceFileEntity = new ToolConsequenceFileEntity();
                            toolConsequenceFileEntity.setName(StringUtils.isEmpty(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath())) ? "-" : FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolConsequenceFileEntity.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolConsequenceFileEntity.setFolder(false);
                            toolConsequenceFileEntity.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolConsequenceFileEntity.setParentNode(parentNode);
                            toolConsequenceFileEntity.setToolConsequenceEntity(toolConsequenceAgrs);
                            toolConsequenceFileEntityList.add(toolConsequenceFileRepository.save(toolConsequenceFileEntity));
                        }
                    } else {
                        // 路径节点，先判断是否存在该节点
                        if (hasToolConsequenceFileByNameAndExtensionAndParentNodeAndToolConsequence(path, "?", parentNode, toolConsequenceAgrs)) {
                            parentNode = getToolConsequenceFileByNameAndExtensionAndToolConsequenceEntity(path, "?", parentNode, toolConsequenceAgrs);
                        } else {
                            ToolConsequenceFileEntity toolConsequenceFileEntity = new ToolConsequenceFileEntity();
                            toolConsequenceFileEntity.setName(path);
                            toolConsequenceFileEntity.setExtension("?");
                            toolConsequenceFileEntity.setFolder(true);
                            toolConsequenceFileEntity.setParentNode(parentNode);
                            toolConsequenceFileEntity.setToolConsequenceEntity(toolConsequenceAgrs);
                            parentNode = toolConsequenceFileRepository.save(toolConsequenceFileEntity);
                            toolConsequenceFileEntityList.add(toolConsequenceFileEntity);
                        }
                    }
                }
            }
        }
        return toolConsequenceFileEntityList;
    }

    public File exportConsequenceFileById(String toolConsequenceFileId) throws IOException {
        ToolConsequenceFileEntity toolConsequenceFileEntity = getToolConsequenceFileById(toolConsequenceFileId);
        if (toolConsequenceFileEntity.isFolder()) {
            //初始化导出目录
            File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separator + UUID.randomUUID().toString());
            exportDir.mkdirs();
            exportConsequenceFile(toolConsequenceFileEntity, exportDir);
            return CompressUtils.compress(exportDir, new File(FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis() + ".zip"));
        } else {
            File exportFile = new File(FileUtils.getTempDirectoryPath() + File.separator + toolConsequenceFileEntity.getName() + "." + toolConsequenceFileEntity.getFileEntity().getType());
            FileUtils.copyFile(new File(toolConsequenceFileEntity.getFileEntity().getLocalPath()), exportFile);
            return exportFile;
        }
    }

    //导出成果文件
    public File exportConsequenceFile(ToolConsequenceFileEntity toolConsequenceFileEntity, File exportDir) throws IOException {
        if (toolConsequenceFileEntity.isFolder()) {
            for (ToolConsequenceFileEntity toolConsequenceFile : getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceFileEntity.getToolConsequenceEntity(), toolConsequenceFileEntity.getId())) {
                exportConsequenceFile(toolConsequenceFile, exportDir);
            }
        } else {
            File file = new File(exportDir.getAbsolutePath() + File.separator + FormatUtils.getConsequenceFileRelativePath(toolConsequenceFileEntity, ""));
            FileUtils.copyFile(new File(toolConsequenceFileEntity.getFileEntity().getLocalPath()), file);
        }
        return exportDir;
    }

    public boolean hasToolConsequenceFileByNameAndExtensionAndParentNodeAndToolConsequence(String name, String extension, ToolConsequenceFileEntity parentNode, ToolConsequenceEntity toolConsequenceEntity) {
        if (!StringUtils.isEmpty(name) || !StringUtils.isEmpty(extension)) {
            return false;
        }
        return toolConsequenceFileRepository.existsByNameAndExtensionAndParentNodeAndToolConsequenceEntity(name, extension, parentNode, toolConsequenceEntity);
    }

    // 根据名称、父节点及工具成果查询文件
    public ToolConsequenceFileEntity getToolConsequenceFileByNameAndExtensionAndToolConsequenceEntity(String name, String extension, ToolConsequenceFileEntity parentNode, ToolConsequenceEntity toolConsequenceEntity) {
        return toolConsequenceFileRepository.findByNameAndExtensionAndParentNodeAndToolConsequenceEntity(name, extension, parentNode, toolConsequenceEntity).get();
    }

    //根据工具成果和父节点查询工具文件
    public List<ToolConsequenceFileEntity> getToolConsequenceFileByParentNodeAndToolConsequence(ToolConsequenceEntity toolConsequenceEntity,String parentNodeId) {
        ToolConsequenceFileEntity parentNode = hasToolConsequenceFileById(parentNodeId) ? getToolConsequenceFileById(parentNodeId) : null;
        List<ToolConsequenceFileEntity> list = toolConsequenceFileRepository.findByParentNodeAndToolConsequenceEntity(parentNode,toolConsequenceEntity);
        return list;
    }

    public ToolConsequenceFileEntity getToolConsequenceFileById(String toolConsequenceFileId) {
        if (!hasToolConsequenceFileById(toolConsequenceFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_CONSEQUENCE_FILE_ID_NOT_FOUND + toolConsequenceFileId);
        }
        return toolConsequenceFileRepository.findById(toolConsequenceFileId).get();
    }

    public boolean hasToolConsequenceFileById(String toolConsequenceFileId) {
        if (StringUtils.isEmpty(toolConsequenceFileId)) {
            return false;
        }
        return toolConsequenceFileRepository.existsById(toolConsequenceFileId);
    }

    //导出通过成果导出成果文件
    public File exportToolConsequenceFileByToolConsequence(ToolConsequenceEntity toolConsequenceEntity, UserEntity userEntity) throws IOException {
        //todo:初始化导出目录
        File exportPath = new File(FileUtils.getTempDirectoryPath() + File.separator + toolConsequenceEntity.getName());
        exportPath.mkdirs();
        //导出成果文件
        List<ToolConsequenceFileEntity> list = getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceEntity, null);
        System.out.println("长度" + list.size());
        for (ToolConsequenceFileEntity toolConsequenceFile : getToolConsequenceFileByParentNodeAndToolConsequence(toolConsequenceEntity, null)) {
            exportConsequenceFile(toolConsequenceFile, exportPath);
        }
        String username = userEntity.getUsername();
        String description = "用户：" + username + "、导出工具文件名称：" + toolConsequenceEntity.getName();
        UserActionLogEntity userActionLogEntity = new UserActionLogEntity();
        userActionLogEntity.setUsername(username);
        userActionLogEntity.setObject(UserActionLogService.USER_OBJECT);
        userActionLogEntity.setType(UserActionLogService.CREATE_TYPE);
        userActionLogEntity.setDescription(description);
        userActionLogService.saveUserActionLog(userActionLogEntity);
        return CompressUtils.compress(exportPath, new File(FileUtils.getTempDirectoryPath() + File.separator + toolConsequenceEntity.getName() + ".zip"));
    }

    //根据用户id查询工具成果文件
    public List<ToolConsequenceFileEntity> getToolConsequenceFileByUserId(String userId) {
        return toolConsequenceFileRepository.findAllByUserId(userId);
    }
}
