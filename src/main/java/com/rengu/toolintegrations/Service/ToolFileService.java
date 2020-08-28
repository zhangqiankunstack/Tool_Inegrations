package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.FileMetaEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.ToolFileEntity;
import com.rengu.toolintegrations.Repository.ToolFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Autowired
    public ToolFileService(ToolFileRepository toolFileRepository, FileService fileService) {
        this.toolFileRepository = toolFileRepository;
        this.fileService = fileService;
    }

    //根据工具名称或者文件名称组合查询
    public List<ToolFileEntity> getToolFileFuzzQueryByToolNameOrByFileName(String toolName, String fileName, String userId, Pageable pageable){
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        //return toolFileRepository.findByNameBy;
        return null;
    }

    // 根据组件父节点保存文件
    public List<ToolFileEntity> saveToolFilesByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, List<FileMetaEntity> fileMetaEntityList){
        List<ToolFileEntity> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolFileEntity parentNode = hasComponentFileById(parentNodeId) ? getComponentFileById(parentNodeId) : null;
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
        //return saveComponentFilesHistory(componentFileEntityList);
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
    public ToolFileEntity getComponentFileById(String toolFileId) {
        if (!hasToolFileById(toolFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_FILE_ID_NOT_FOUND + toolFileId);
        }
        return toolFileRepository.findById(toolFileId).get();
    }

    // 根据Id查询组件文件是否存在
    public boolean hasToolFileById(String toolFileId) {
        if (StringUtils.isEmpty(toolFileId)) {
            return false;
        }
        return toolFileRepository.existsById(toolFileId);
    }

    // 根据Id查询组件文件是否存在
    public boolean hasComponentFileById(String componentFileId) {
        if (StringUtils.isEmpty(componentFileId)) {
            return false;
        }
        return toolFileRepository.existsById(componentFileId);

    }
}
