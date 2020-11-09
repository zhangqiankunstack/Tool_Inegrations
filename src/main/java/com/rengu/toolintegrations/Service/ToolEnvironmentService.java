//package com.rengu.toolintegrations.Service;
//
//import com.rengu.toolintegrations.Entity.UserEntity;
//import com.rengu.toolintegrations.Repository.ToolEnvironmentRepository;
//import com.rengu.toolintegrations.Utils.ApplicationMessages;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.List;
//
//@Service
//public class ToolEnvironmentService {
//
//    private final ToolEnvironmentRepository toolEnvironmentRepository;
//
//    public ToolEnvironmentService( ToolEnvironmentRepository toolEnvironmentRepository) {
//        this.toolEnvironmentRepository = toolEnvironmentRepository;
//    }

//    //保存工具环境
//    public ToolEnvironmentEntity saveToolAchievements(UserEntity userEntity, ToolEnvironmentEntity toolAchievementsArgs) {
//        if (StringUtils.isEmpty(toolAchievementsArgs.getName())) {
//            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_NOT_NULL);
//        }
//        //判断是否存在
//        if (hasToolAchievementsByNameAndTypeAndVersion(toolAchievementsArgs.getName(), toolAchievementsArgs.getType(), toolAchievementsArgs.getVersion())) {
//            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_AND_TYPE_AND_VERSION_EXIST + toolAchievementsArgs.getName() + "-" + toolAchievementsArgs.getVersion() + "-" + toolAchievementsArgs.getType());
//        }
//        //工具上传人员
//        toolAchievementsArgs.setUserEntity(userEntity);
//        return toolEnvironmentRepository.save(toolAchievementsArgs);
//    }

//    //查看上传工具下的上传环境
//    public List<ToolEnvironmentEntity> findToolAchievements(String toolFileId){
//        if (StringUtils.isEmpty(toolFileId)){
//            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND);
//        }
//        return toolEnvironmentRepository.findByToolFileEntity(toolFileId);
//    }
//
//    public boolean hasToolAchievementsByNameAndTypeAndVersion(String name, String type, String version) {
//        if (!StringUtils.isEmpty(name) || !StringUtils.isEmpty(type) || !StringUtils.isEmpty(version)) {
//            return false;
//        }
//        return toolEnvironmentRepository.existsByNameAndTypeAndVersion(name, type, version);
//    }
//
//    public ToolEnvironmentEntity getToolAchievementsById(String id) {
//        if (!hasToolAchievementsById(id)) {
//            throw new RuntimeException(ApplicationMessages.TOOL_AVHIEVEMENTS_NAME_ID_NOT_FOUND + id);
//        }
//        return toolEnvironmentRepository.findById(id).get();
//    }
//
//    public boolean hasToolAchievementsById(String id) {
//        if (StringUtils.isEmpty(id)) {
//            return false;
//        }
//        return toolEnvironmentRepository.existsById(id);
//    }
//}
