package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolComment;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolCommentRepository extends JpaRepository<ToolComment,String> {

//    Page<ToolComment> findByToolFileId(String toolFileId, Pageable pageable);

    List<ToolComment> findAllByToolEntity(ToolEntity toolEntity, Sort sort);

    @Query(value = "SELECT AVG(star_grade) FROM tool_comment where ( tool_entity_id = ?1)", countQuery = "SELECT AVG(star_grade) FROM tool_comment where (tool_entity_id = ?1)",nativeQuery = true)
    double findAllAvg(String toolId);

    @Query(value = "SELECT COUNT(star_grade) FROM tool_comment where ( tool_entity_id = ?1)", countQuery = "SELECT COUNT(star_grade) FROM tool_comment where (tool_entity_id = ?1)",nativeQuery = true)
    int findCount(String toolId);

    List<ToolComment> findAllByUserEntity(UserEntity userEntity);
}
