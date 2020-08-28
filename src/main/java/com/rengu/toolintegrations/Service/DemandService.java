package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.DemandEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Repository.DemandRepository;
import com.rengu.toolintegrations.Utils.ApplicationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * Author: Zhangqiankun
 * Date: 2020/8/26 17:46
 */
@Service
public class DemandService {
    private final DemandRepository demandRepository;
    private final ToolService toolService;
    @Autowired
    public DemandService(DemandRepository demandRepository, ToolService toolService) {
        this.demandRepository = demandRepository;
        this.toolService = toolService;
    }

    //保存工作流信息
    public DemandEntity saveWorkFlow(DemandEntity demandArgs) {
        if (StringUtils.isEmpty(demandArgs.getName())) {
            throw new RuntimeException(ApplicationMessages.WORKFLOW_NAME_NOT_NULL);
        }
        if (StringUtils.isEmpty(demandArgs.getType())) {
            throw new RuntimeException(ApplicationMessages.WORKFLOW_TYPE_NOT_NULL_OR_ARGS_NOT_FOUND);
        }
        //判断工作流是否已经存在
        if(hasDemandByNameAndTypeAndDeleted(demandArgs.getName(),demandArgs.getType(),false)){
        }

        return demandRepository.save(demandArgs);
    }

    //绑定工具类
    public DemandEntity bindToolsBaseinfoById(String demandId,String toolId){
        DemandEntity demandEntity = demandRepository.findById(demandId).get();
        ToolEntity toolEntity = toolService.getToolById(toolId);
        demandEntity.setToolEntity(toolEntity);
        return demandEntity;
    }

    //取消绑定
    public DemandEntity unbindToolsBaseinfoById(String demandId){
        DemandEntity demandEntity = demandRepository.findById(demandId).get();
        demandEntity.setToolEntity(null);
        return demandEntity;
    }


    //根据id查询需求
    public DemandEntity getDemandById(String demandId){
        if(!hasDemandId(demandId)){
            throw new RuntimeException(ApplicationMessages.DEMAND_ID_ARGS_NOT_FOUND + demandId);
        }
        return demandRepository.findById(demandId).get();
    }

    //判断id是否存在
    public boolean hasDemandId(String demandId){
        if(StringUtils.isEmpty(demandId)){
            return false;
        }
        return demandRepository.existsById(demandId);
    }

    //根据工作流名称，类型，是否删除以及工具判断工作流是否存在
    public boolean hasDemandByNameAndTypeAndDeleted(String name,String type,boolean deleted){
        if(StringUtils.isEmpty(name)|| StringUtils.isEmpty(type)){
            return false;
        }
        return demandRepository.existsByNameAndTypeAndDeleted(name,type,deleted);
    }
}
