package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolAchievementsFileRepository;
import com.rengu.toolintegrations.Repository.ToolFileRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import com.rengu.toolintegrations.Utils.CompressUtils;
import com.rengu.toolintegrations.Utils.FormatUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private final ToolFileRepository toolFileRepository;
    private final ToolFileService toolFileService;

    public ToolAchievementsFileService(ToolAchievementsFileRepository achievementsFileRepository, FileService fileService, ToolFileRepository toolFileRepository,ToolFileService toolFileService) {
        this.achievementsFileRepository = achievementsFileRepository;
        this.fileService = fileService;
        this.toolFileRepository = toolFileRepository;
        this.toolFileService = toolFileService;
    }

    // 根据组件父节点保存文件
//    public List<ToolAchievementsFile> saveToolAchievementsFileByParentNodeAndTool(ToolAchievements toolAchievements, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
//        List<ToolAchievementsFile> toolFileEntityList = new ArrayList<>();
//        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
//            ToolAchievementsFile parentNode = hasToolAchievementsFileById(parentNodeId) ? getToolAchievementsFileById(parentNodeId) : null;
//            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
//            for (int i = 0; i < splitPaths.length; i++) {
//                String path = splitPaths[i];
//                if (!StringUtils.isEmpty(path)) {
//                    if (i == splitPaths.length - 1) {
//                        // 文件节点，先判断是否存在该节点
//                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolAchievements(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolAchievements)) {
//                            ToolAchievementsFile toolAchievementsFile = getToolFileByNameAndExtensionAndToolAchievements(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolAchievements);
//                            toolAchievementsFile.setName(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
//                            toolAchievementsFile.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
//                            toolAchievementsFile.setFolder(false);
//                            toolAchievementsFile.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
//                            toolFileEntityList.add(achievementsFileRepository.save(toolAchievementsFile));
//                        } else {
//                            ToolAchievementsFile toolAchievementsFile = new ToolAchievementsFile();
//                            toolAchievementsFile.setName(StringUtils.isEmpty(FilenameUtils.getBaseName(fileMetaEntity.getRelativePath())) ? "-" : FilenameUtils.getBaseName(fileMetaEntity.getRelativePath()));
//                            toolAchievementsFile.setExtension(FilenameUtils.getExtension(fileMetaEntity.getRelativePath()));
//                            toolAchievementsFile.setFolder(false);
//                            toolAchievementsFile.setFileEntity(fileService.getFileById(fileMetaEntity.getFileId()));
//                            toolAchievementsFile.setParentNode(parentNode);
//                            toolAchievementsFile.setToolAchievements(toolAchievements);
//                            toolFileEntityList.add(achievementsFileRepository.save(toolAchievementsFile));
//                        }
//                    } else {
//                        // 路径节点，先判断是否存在该节点
//                        if (hasToolAchievementsByNameAndExtensionAndParentNodeAndToolAchievements(path, "?", parentNode, toolAchievements)) {
//                            parentNode = getToolFileByNameAndExtensionAndToolAchievements(path, "?", parentNode, toolAchievements);
//                        } else {
//                            ToolAchievementsFile toolAchievementsFile = new ToolAchievementsFile();
//                            toolAchievementsFile.setName(path);
//                            toolAchievementsFile.setExtension("?");
//                            toolAchievementsFile.setFolder(true);
//                            toolAchievementsFile.setParentNode(parentNode);
//                            toolAchievementsFile.setToolAchievements(toolAchievements);
//                            parentNode = achievementsFileRepository.save(toolAchievementsFile);
//                            toolFileEntityList.add(toolAchievementsFile);
//                        }
//                    }
//                }
//            }
//        }
//        //生成历史版本很多，返回给我一次保存
//        return toolFileEntityList;
//    }

    public List<ToolFileEntity> saveToolAchievementsFileByParentNodeAndTool(ToolEntity toolEntity, String parentNodeId, List<FileMetaEntity> fileMetaEntityList) {
        List<ToolFileEntity> toolFileEntityList = new ArrayList<>();
        for (FileMetaEntity fileMetaEntity : fileMetaEntityList) {
            ToolFileEntity parentNode =toolFileService.hasToolFileById(parentNodeId) ? toolFileService.getToolFileById(parentNodeId) : null;
            String[] splitPaths = fileMetaEntity.getRelativePath().split("/");
            for (int i = 0; i < splitPaths.length; i++) {
                String path = splitPaths[i];
                if (!StringUtils.isEmpty(path)) {
                    if (i == splitPaths.length - 1) {
                        // 文件节点，先判断是否存在该节点
                        if (toolFileService.hasToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity)) {
                            ToolFileEntity toolFileEntity = toolFileService.getToolFileByNameAndExtensionAndParentNodeAndTool(FilenameUtils.getBaseName(path), FilenameUtils.getExtension(path), parentNode, toolEntity);
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
                        if (toolFileService.hasToolFileByNameAndExtensionAndParentNodeAndTool(path, "?", parentNode, toolEntity)) {
                            parentNode = toolFileService.getToolFileByNameAndExtensionAndParentNodeAndTool(path, "?", parentNode, toolEntity);
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


    public ToolAchievementsFile getToolAchievementsFileById(String toolAchievementsFileId) {
        if (!hasToolAchievementsFileById(toolAchievementsFileId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_FILE_NAME_ID_NOT_FOUND);
        }
        return achievementsFileRepository.findById(toolAchievementsFileId).get();
    }

    //导出工具成果文件
    public File exportAchievementsFileById(String achievementsFilesId) throws IOException {
        ToolAchievementsFile toolAchievementsFile = getToolAchievementsFileById(achievementsFilesId);
        if (toolAchievementsFile.isFolder()) {
            //初始化导出目录
            File exportDir = new File(FileUtils.getTempDirectoryPath() + File.separator + UUID.randomUUID().toString());
            exportDir.mkdirs();
            exportAchievementsFile(toolAchievementsFile,exportDir);
            return CompressUtils.compress(exportDir,new File(FileUtils.getTempDirectoryPath() + File.separator + System.currentTimeMillis() + ".zip"));
        }else {
            File exportFile = new File(FileUtils.getTempDirectoryPath() + File.separator + toolAchievementsFile.getName() + "." + toolAchievementsFile.getFileEntity().getType());
            FileUtils.copyFile(new File(toolAchievementsFile.getFileEntity().getLocalPath()), exportFile);
            return exportFile;
        }
    }

    //导出成果文件
    public File exportAchievementsFile(ToolAchievementsFile toolAchievementsFile,File exportDir)throws IOException {
        //检查是否为文件夹
        if (toolAchievementsFile.isFolder()){
            for (ToolAchievementsFile toolAchievementsFile1:getToolAchievementsFilesByParentNodeAndComponent(toolAchievementsFile.getId(),toolAchievementsFile.getToolAchievements())){
                exportAchievementsFile(toolAchievementsFile1,exportDir);
            }
        }else {
        File file=new File(exportDir.getAbsolutePath()+File.separator+ FormatUtils.getComponentFileRelativePath(toolAchievementsFile,""));
        FileUtils.copyFile(new File(toolAchievementsFile.getFileEntity().getLocalPath()),file);
        }
        return exportDir;
    }

    // 查询父节点和组件查询组件文件
    public List<ToolAchievementsFile> getToolAchievementsFilesByParentNodeAndComponent(String parentNodeId, ToolAchievements toolAchievementsFile) {
        ToolAchievementsFile parentNode = hasToolAchievementsFileById(parentNodeId) ? getToolAchievementsFileById(parentNodeId) : null;
        return achievementsFileRepository.findByParentNodeAndToolAchievements(parentNode, toolAchievementsFile);
    }

    //查看成果文件
    public List<ToolFileEntity> findToolAchievementsFilesByToolAchievementsId(String toolId,@PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable){
        if (StringUtils.isEmpty(toolId)){
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_ID_NOT_FOUND);
        }
        return toolFileService.getToolFilesByToolId(toolId,pageable);
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
