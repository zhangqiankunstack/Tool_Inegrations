package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 17:30
 */
@Service
public class ToolService {
    private final ToolRepository toolRepository;

    @Autowired
    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    //保存工具
    public ToolEntity saveTool(UserEntity userEntity, ToolEntity toolArge) {
        if (StringUtils.isEmpty(toolArge.getName())) {
            throw new RuntimeException(ApplicationMessages.TOOL_NAME_EXISTED);
        }
        //判断是否存在
        if (hasToolByNameAndVersionAndTpyeAndUserEntity(toolArge.getName(), toolArge.getVersion(), toolArge.getType(), false, userEntity)) {
            throw new RuntimeException(ApplicationMessages.TOOL_NAME_AND_VERSION_EXISTED + toolArge.getName() + "-" + toolArge.getVersion() + "-" + toolArge.getType());
        }
        //工具所有人员
        toolArge.setUserEntity(userEntity);
        return toolRepository.save(toolArge);
    }

    //根据工具id修改工具
    public ToolEntity updateToolById(String toolId, ToolEntity toolAgrs) {
        ToolEntity toolEntity = getToolById(toolId);
        if (!StringUtils.isEmpty(toolAgrs.getName())) {
            toolEntity.setName(toolAgrs.getName());
        }
        if (!StringUtils.isEmpty(toolAgrs.getType())) {
            toolEntity.setType(toolAgrs.getType());
        }
        //根据工具名称以及工具类型是否删除和用户判断工具是否存在
        if (hasToolByNameAndVersionAndTpyeAndUserEntity(toolAgrs.getName(), toolAgrs.getVersion(), toolAgrs.getType(), false, toolEntity.getUserEntity())) {
            throw new RuntimeException(ApplicationMessages.TOOL_NAME_AND_VERSION_EXISTED + toolAgrs.getName() + "-" + toolAgrs.getVersion());
        }
        toolEntity.setVersion(toolAgrs.getVersion());
        toolEntity.setType(toolAgrs.getType());
        toolEntity.setDescription(toolAgrs.getDescription());
        toolEntity.setNewFeatures(toolAgrs.getNewFeatures());
        return toolRepository.save(toolEntity);
    }

    public ToolEntity getToolById(String toolId) {
        if (!hasToolById(toolId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND + toolId);
        }
        return toolRepository.findById(toolId).get();
    }

    public boolean hasToolById(String toolId) {
        if (StringUtils.isEmpty(toolId)) {
            return false;
        }
        return toolRepository.existsById(toolId);
    }

    //根据工具名，版本号，以及类型判断工具是否存在
    public boolean hasToolByNameAndVersionAndTpyeAndUserEntity(String toolName, String version, String type, boolean deleted, UserEntity userEntity) {
        if (!StringUtils.isEmpty(toolName) || !StringUtils.isEmpty(version) || !StringUtils.isEmpty(type)) {
            return false;
        }
        return toolRepository.existsByNameAndVersionAndTypeAndDeletedAndUserEntity(toolName, version, type, deleted, userEntity);
    }

    //根据用户以及是否删除查询工具
    public List<ToolEntity> getToolAllByDeletedAndUser(boolean deleted, UserEntity userEntity, Pageable pageable) {
        return toolRepository.findByDeletedAndUserEntity(deleted, userEntity, pageable);
    }

    public ToolEntity deleteToolById(String toolId) {
        ToolEntity toolEntity = getToolById(toolId);
        toolEntity.setDeleted(true);
        return toolEntity;
    }

}
