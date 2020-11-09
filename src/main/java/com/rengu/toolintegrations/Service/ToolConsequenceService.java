package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.*;
import com.rengu.toolintegrations.Repository.ToolConsequenceRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/31 17:35
 */
@Service
public class ToolConsequenceService {
    private ToolConsequenceRepository toolConsequenceRepository;
    private final ToolConsequenceFileService toolConsequenceFileService;
    private final ToolDrawingNodeService toolDrawingNodeService;

    @Autowired
    public ToolConsequenceService(ToolConsequenceRepository toolConsequenceRepository, ToolConsequenceFileService toolConsequenceFileService, ToolDrawingNodeService toolDrawingNodeService) {
        this.toolConsequenceRepository = toolConsequenceRepository;
        this.toolConsequenceFileService = toolConsequenceFileService;
        this.toolDrawingNodeService = toolDrawingNodeService;
    }
    //根据用户删除工具成果
    public List<ToolConsequenceEntity> cleanToolConsequenceByUserEntity(UserEntity userEntity) throws IOException {
        List<ToolConsequenceEntity> toolConsequenceEntityList = getToolConsequenceByUserEntity(userEntity);
        for(ToolConsequenceEntity toolConsequenceEntity:toolConsequenceEntityList){
            //通过成果去删除成果文件
            toolConsequenceFileService.deletedToolConsequenceFileByToolConsequence(toolConsequenceEntity);
            toolDrawingNodeService.deletedToolDrawingNodeByToolConse(toolConsequenceEntity);
            toolConsequenceRepository.delete(toolConsequenceEntity);
        }
        return toolConsequenceEntityList;
    }

    //根据用户获取工具成果
    public List<ToolConsequenceEntity> getToolConsequenceByUserEntity(UserEntity userEntity) {
        return toolConsequenceRepository.findAllByUserEntity(userEntity);
    }

    public ToolConsequenceEntity getToolConsequenceById(String toolConsequenceId) {
        if (!hasToolConsequenceById(toolConsequenceId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_CONSEQUENCE_ID_NOT_FOUND + toolConsequenceId);
        }
        return toolConsequenceRepository.findById(toolConsequenceId).get();
    }

    public ToolConsequenceEntity getToolConsequence(String toolConsequenceId) {
        return toolConsequenceRepository.findById(toolConsequenceId).orElse(null);
    }

    public boolean hasToolConsequenceById(String toolConsequenceId) {
        if (StringUtils.isEmpty(toolConsequenceId)) {
            return false;
        }
        return toolConsequenceRepository.existsById(toolConsequenceId);
    }
}
