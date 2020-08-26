package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public ToolEntity saveTool(UserEntity userEntity,ToolEntity toolArge){
        if (StringUtils.isEmpty(toolArge.getName())) {
            throw new RuntimeException(ApplicationMessages.TOOL_NAME_EXISTED);
        }
        //判断是否存在
        if(hasToolByNameAndVersionAndTpye(toolArge.getName(),toolArge.getVersion(),toolArge.getType())){
            throw new RuntimeException(ApplicationMessages.COMPONENT_NAME_AND_VERSION_EXISTED + toolArge.getName() + "-" + toolArge.getVersion()+ "-" +toolArge.getType());
        }
//        if (hasToolByNameAndVersionAndDeletedAndProject(componentEntity.getName(), componentEntity.getVersion(), false, projectEntity)) {
//            throw new RuntimeException(ApplicationMessages.COMPONENT_NAME_AND_VERSION_EXISTED + componentEntity.getName() + "-" + componentEntity.getVersion());
//        }
        //工具所有人员
        toolArge.setUserEntity(userEntity);
        return toolRepository.save(toolArge);
    }

    //根据工具id查询工具
    public ToolEntity getToolById(String toolId){
        if(!hasToolById(toolId)){
            throw new RuntimeException(ApplicationMessages.TOOL_ID_NOT_FOUND + toolId);
        }
        return toolRepository.findById(toolId).get();
    }

    //根据工具id判断工具是否存在
    public boolean hasToolById(String toolId){
        if(StringUtils.isEmpty(toolId)){
            return false;
        }
        return toolRepository.existsById(toolId);
    }

    //根据工具名，版本号，以及类型判断工具是否存在
    public boolean hasToolByNameAndVersionAndTpye(String toolName,String version,String type){
        if(!StringUtils.isEmpty(toolName) ||!StringUtils.isEmpty(version) ||!StringUtils.isEmpty(type)){
            return false;
        }
        return toolRepository.existsByNameAndVersionAndType(toolName,version,type);
    }

    //根据组件id删除组件
    public ToolEntity deleteToolById(String toolId) {
        ToolEntity toolEntity = getToolById(toolId);
        toolEntity.setDeleted(true);
        return toolEntity;
    }
}
