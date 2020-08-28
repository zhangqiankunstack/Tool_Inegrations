package com.rengu.toolintegrations.Service;

import com.rengu.toolintegrations.Entity.ToolComment;
import com.rengu.toolintegrations.Entity.UserEntity;
import com.rengu.toolintegrations.Repository.ToolCommentRepository;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ToolCommentService
 * @Description TODO
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
    public ToolComment saveToolComment(UserEntity userEntity, ToolComment toolCommentArgs){
        toolCommentArgs.setUserEntity(userEntity);
        return toolCommentRepository.save(toolCommentArgs);
    }

    //查看当前工具下的评论
    public List<ToolComment> findToolCommentByToolFileId(String toolFileId){
        List<ToolComment> list=toolCommentRepository.findByToolFileId(toolFileId);
        IntSummaryStatistics statistics=list.stream().mapToInt(x->x.getStarGrade()).summaryStatistics();
        list.forEach(p->p.setAverage(statistics.getAverage()));
        return list;
    }

}
