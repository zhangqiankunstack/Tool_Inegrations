package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolAchievements;
import com.rengu.toolintegrations.Entity.ToolAchievementsFile;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolAchievementsRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ToolAchievementsService {

    private final ToolAchievementsRepository toolAchievementsRepository;

    public ToolAchievementsService(ToolAchievementsRepository toolAchievementsRepository) {
        this.toolAchievementsRepository = toolAchievementsRepository;
    }

    //保存工具成果
    public ToolAchievements saveToolAchievements(UserEntity userEntity, ToolAchievements toolAchievementsArgs) {
        if (StringUtils.isEmpty(toolAchievementsArgs.getName())) {
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_NOT_NULL);
        }
        //判断是否存在
        if (hasToolAchievementsByNameAndTypeAndVersion(toolAchievementsArgs.getName(), toolAchievementsArgs.getType(), toolAchievementsArgs.getVersion())) {
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_AND_TYPE_AND_VERSION_EXIST + toolAchievementsArgs.getName() + "-" + toolAchievementsArgs.getVersion() + "-" + toolAchievementsArgs.getType());
        }
        //工具上传人员
        toolAchievementsArgs.setUserEntity(userEntity);
        return toolAchievementsRepository.save(toolAchievementsArgs);
    }

    //查看上传工具下的上传成果
    public List<ToolAchievements> findToolAchievements(String toolFileId){
        if (StringUtils.isEmpty(toolFileId)){
            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND);
        }
        return toolAchievementsRepository.findByToolFileEntity(toolFileId);
    }

    public boolean hasToolAchievementsByNameAndTypeAndVersion(String name, String type, String version) {
        if (!StringUtils.isEmpty(name) || !StringUtils.isEmpty(type) || !StringUtils.isEmpty(version)) {
            return false;
        }
        return toolAchievementsRepository.existsByNameAndTypeAndVersion(name, type, version);
    }

    public ToolAchievements getToolAchievementsById(String id) {
        if (!hasToolAchievementsById(id)) {
            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_ID_NOT_FOUND + id);
        }
        return toolAchievementsRepository.findById(id).get();
    }

    public boolean hasToolAchievementsById(String id) {
        if (StringUtils.isEmpty(id)) {
            return false;
        }
        return toolAchievementsRepository.existsById(id);
    }
}
