package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolCommentRepository extends JpaRepository<ToolComment,String> {
    List<ToolComment> findByToolFileId(String toolFileId);
}
