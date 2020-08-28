package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolAchievementsFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ToolAchievementsFileService
 * @Description TODO 工具成果业务逻辑层
 * @Author yyc
 * @Date 2020/8/26 13:56
 * @Version 1.0
 */
@Service
public class ToolAchievementsFileService {
    private final ToolAchievementsFileRepository achievementsFileRepository;
    private final FileService fileService;

    public ToolAchievementsFileService(ToolAchievementsFileRepository achievementsFileRepository, FileService fileService) {
        this.achievementsFileRepository = achievementsFileRepository;
        this.fileService = fileService;
    }

    // 根据组件父节点保存文件
    public List<ToolAchievementsFile> saveToolAchievementsFileByParentNodeAndTool(ToolAchievements toolAchievements, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        List<ToolAchievementsFile> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolAchievementsFile parentNode = hasToolAchievementsFileById(parentNodeId) ? getToolAchievementsFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolAchievements(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolAchievements)) {
                            ToolAchievementsFile toolAchievementsFile = getToolFileByNameAndExtensionAndToolAchievements(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolAchievements);
                            toolAchievementsFile.setName(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolAchievementsFile.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolAchievementsFile.setFolder(false);
                            toolAchievementsFile.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolFileEntityList.add(achievementsFileRepository.save(toolAchievementsFile));
                        } else {
                            ToolAchievementsFile toolAchievementsFile = new ToolAchievementsFile();
                            toolAchievementsFile.setName(StringUtils.isEmpty(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath())) ? "-" : FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
                            toolAchievementsFile.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
                            toolAchievementsFile.setFolder(false);
                            toolAchievementsFile.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
                            toolAchievementsFile.setParentNode(parentNode);
                            toolAchievementsFile.setToolAchievements(toolAchievements);
                            toolFileEntityList.add(achievementsFileRepository.save(toolAchievementsFile));
                        }
                    } else {
                        // 路径节点，先判断是否存在该节点
                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolAchievements(path, "?", parentNode, toolAchievements)) {
                            parentNode = getToolFileByNameAndExtensionAndToolAchievements(path, "?", parentNode, toolAchievements);
                        } else {
                            ToolAchievementsFile toolAchievementsFile = new ToolAchievementsFile();
                            toolAchievementsFile.setName(path);
                            toolAchievementsFile.setExtension("?");
                            toolAchievementsFile.setFolder(true);
                            toolAchievementsFile.setParentNode(parentNode);
                            toolAchievementsFile.setToolAchievements(toolAchievements);
                            parentNode = achievementsFileRepository.save(toolAchievementsFile);
                            toolFileEntityList.add(toolAchievementsFile);
                        }
                    }
                }
            }
        }
        //生成历史版本很多，返回给我一次保存
        return toolFileEntityList;
    }

    public ToolAchievementsFile getToolAchievementsFileById(String toolAchievementsFileId) {
        if (!hasToolAchievementsFileById(toolAchievementsFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_FILE_NAME_ID_NOT_FOUND);
        }
        return achievementsFileRepository.findById(toolAchievementsFileId).get();
    }

    // 根据Id查询工具成果文件是否存在
    public boolean hasToolAchievementsFileById(String toolAchievementsFileId) {
        if (StringUtils.isEmpty(toolAchievementsFileId)) {
            return false;
        }
        return achievementsFileRepository.existsById(toolAchievementsFileId);

    }

    public boolean hasToolAchievementsByNameAndExtensionAndParentNodeAndToolAchievements(String name, String extension, ToolAchievementsFile parentNode, ToolAchievements achievements) {
        if (!StringUtils.isEmpty(name) || !StringUtils.isEmpty(extension)) {
            return false;
        }
        return achievementsFileRepository.existsByNameAndExtensionAndParentNodeAndToolAchievements(name, extension, parentNode, achievements);
    }

    // 根据名称、父节点及组件查询文件
    public ToolAchievementsFile getToolFileByNameAndExtensionAndToolAchievements(String name, String extension, ToolAchievementsFile parentNode, ToolAchievements achievements) {
        return achievementsFileRepository.findByNameAndExtensionAndParentNodeAndToolAchievements(name, extension, parentNode, achievements).get();
    }

}
