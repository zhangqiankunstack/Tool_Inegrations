package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author: Zhangqiankun
 * Date: 2020/8/25 17:30
 */
@Service
public class ToolService {
    private final ToolRepository toolRepository;
    private final ToolFileService toolFileService;
    private final ToolEnvironmentFileService toolEnvironmentFileService;
    private final ToolDrawingNodeService toolDrawingNodeService;
    private final ToolCommentService toolCommentService;

    @Autowired
    public ToolService(ToolRepository toolRepository, ToolFileService toolFileService, ToolEnvironmentFileService toolEnvironmentFileService, ToolDrawingNodeService toolDrawingNodeService, ToolCommentService toolCommentService) {
        this.toolRepository = toolRepository;
        this.toolFileService = toolFileService;
        this.toolEnvironmentFileService = toolEnvironmentFileService;
        this.toolDrawingNodeService = toolDrawingNodeService;
        this.toolCommentService = toolCommentService;
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
        //工具所属人员
        toolArge.setUserEntity(userEntity);
        String parentNodeId=null;
        toolRepository.save(toolArge);
        toolFileService.saveToolFileByTool(toolArge,parentNodeId);
        return toolArge;
    }

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

    //根据id清除工具
    public ToolEntity cleanToolById(String toolId) throws IOException {
        ToolEntity toolEntity = getToolById(toolId);
        //TODO:删除工具文件
        toolFileService.cleanToolFileByToolEntity(toolEntity);
        //TODO：删除工具环境类
        toolEnvironmentFileService.cleanToolConsequenceFileByToolEntity(toolEntity);
        //TODO：删除绘图节点
        toolDrawingNodeService.cleanToolWorkFlowNodeByToolEntity(toolEntity);
        //TODO：删除评论
        toolCommentService.cleanToolComentByToolEntity(toolEntity);
        //TODO:删除用户下载次数表信息
        toolRepository.delete(toolEntity);
        return toolEntity;
    }

    public ToolEntity getToolById(String toolId) {
        if (!hasToolById(toolId)) {
            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND + toolId);
        }
        return toolRepository.findById(toolId).get();
    }

    public ToolEntity getToolByIds(String toolId) {
        return toolRepository.findById(toolId).orElse(null);
    }

    //根据工具类型或者名称组合模糊查询
    public List<ToolEntity> getToolFuzzQueryByToolTypeOrByToolName(String toolTpye, String toolName,boolean deleted, Pageable pageable) {
        return toolRepository.findBytoolTypeAndByNameAndByDeleted(toolTpye, toolName,deleted,pageable);
    }

    //根据用户id，名称，删除类型，模糊查询
    public List<ToolEntity> getToolFuzzQueryByUserIdAndToolNameAndDeleted( String toolName,boolean deleted,String userId, Pageable pageable) {
        return toolRepository.findAllBytoolTypeAndByNameAndByDeleted(toolName,deleted,userId,pageable);
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

    //删除工具
    public ToolEntity deleteToolById(String toolId) {
        ToolEntity toolEntity = getToolById(toolId);
        toolEntity.setDeleted(true);
//        Date deletedTime = new Date();
//        toolEntity.setDetetedTime(deletedTime);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deletedTime = format.format(date);
        toolEntity.setDetetedTime(deletedTime);
        return toolRepository.save(toolEntity);
    }

    //恢复工具
    public ToolEntity restoreToolById(String toolId) {
        ToolEntity toolEntity = getToolById(toolId);
        toolEntity.setDeleted(false);
        return toolRepository.save(toolEntity);
    }

    //查询所有工具
    public List<ToolEntity> getToolAll(boolean deleted) {
        Sort sort = Sort.by(Sort.Direction.DESC,"createTime");
        return toolRepository.findAllByDeleted(deleted,sort);
    }

    //根据工具名模糊查询（管理员）
    public List<ToolEntity> fuzzyToolByToolName(String toolName) {
        return toolRepository.findAllByToolName(toolName);
    }


    public List<ToolEntity> cleanToolByUserEntity(UserEntity userEntity) throws IOException{
        List<ToolEntity> toolEntityList = toolRepository.getToolByUserEntity(userEntity);
        for(ToolEntity toolEntity:toolEntityList){
            cleanToolById(toolEntity.getId());
        }
        return toolEntityList;
    }

    //通过用户id查询工具数量
    public Integer findToolCount(String userId){
      return  toolRepository.countByUserEntity_Id(userId);
    }

    //工具管理，下拉框选择
    public List<String> findToolByType(){
        return toolRepository.findDistinctByType();
    }
}
