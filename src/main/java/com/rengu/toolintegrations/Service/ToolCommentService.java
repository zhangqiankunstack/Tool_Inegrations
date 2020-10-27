package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolComment;
import com.rengu.toolintegrations.Entity.ToolDrawingNodeEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolCommentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName ToolCommentService
 * @Description
 * @Author yyc
 * @Date 2020/8/26 19:02
 * @Version 1.0
 */
@Service
public class ToolCommentService {
    private final ToolCommentRepository toolCommentRepository;


    public ToolCommentService(ToolCommentRepository toolCommentRepository) {
        this.toolCommentRepository = toolCommentRepository;
    }

    //新增工具评论
    public ToolComment saveToolComment(UserEntity userEntity, ToolEntity toolEntity, ToolComment toolCommentArgs) {
        toolCommentArgs.setUserEntity(userEntity);
        toolCommentArgs.setToolEntity(toolEntity);
        return toolCommentRepository.save(toolCommentArgs);
    }


//    //查看当前工具下的评论
//    public Page<ToolComment> findToolCommentByToolFileId(String toolFileId, Pageable pageable) {
//        //获取所有的工具评论集合
//        Page<ToolComment> list = toolCommentRepository.findByToolFileId(toolFileId, pageable);
//        DoubleSummaryStatistics statistics = list.stream().mapToDouble(x -> x.getStarGrade()).summaryStatistics();
//        list.forEach(p -> p.setAverage(statistics.getAverage()));
//        return list;
//    }

    //查看当前工具下的评论
    public List<ToolComment> findToolCommentByToolId(ToolEntity toolEntity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //获取所有的工具评论集合
        List<ToolComment> list = toolCommentRepository.findAllByToolEntity(toolEntity,sort);
        return list;
    }

    //查询平均分
    public double getAvgStarGrade(String toolId) {
        double countNub = toolCommentRepository.findCount(toolId);
        if (countNub != 0) {
            double avgNumber = toolCommentRepository.findAllAvg(toolId);
            System.out.println("平均分" + avgNumber);
            return avgNumber;
        }
        return countNub;
    }

    public List<ToolComment> cleanToolComentByToolEntity(ToolEntity toolEntity) {
        List<ToolComment> toolCommentList = getToolCommentByToolEntity(toolEntity);
        for (ToolComment toolComment : toolCommentList) {
            toolCommentRepository.delete(toolComment);
        }
        return toolCommentList;
    }

    //通过工具获取工具绘图节点
    public List<ToolComment> getToolCommentByToolEntity(ToolEntity toolEntity) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        return toolCommentRepository.findAllByToolEntity(toolEntity,sort);
    }

    public List<ToolComment> cleanToolComentByUserEntity(UserEntity userEntity) {
        List<ToolComment> toolCommentList = getToolCommentByUserEntity(userEntity);
        for(ToolComment toolComment:toolCommentList){
            toolCommentRepository.delete(toolComment);
        }
        return toolCommentList;
    }

    //根据用户名称查询评论
    public List<ToolComment> getToolCommentByUserEntity(UserEntity userEntity){
        return toolCommentRepository.findAllByUserEntity(userEntity);
    }
}
