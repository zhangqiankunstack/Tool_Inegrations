package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.FileEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.ToolFileEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolFileRepository extends JpaRepository<ToolFileEntity, String> {

    boolean existsByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity);

    Optional<ToolFileEntity> findByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity);

//    //根据工具名称或文件名模糊查询
//    @Query(value = "select t2.* from user_entity u1 INNER JOIN tool_entity t1 ON u1.id =t1.user_entity_id INNER JOIN tool_file_entity t2 on t1.id=t2.tool_entity_id WHERE (?1 is null or t2.type Like %?1%) and (?2 is null or t2.`name` Like %?2%) and (?3 is null or u1.id = ?3)", countQuery ="select t2.* from user_entity u1 INNER JOIN tool_entity t1 ON u1.id =t1.user_entity_id INNER JOIN tool_file_entity t2 on t1.id=t2.tool_entity_id WHERE (?1 is null or t2.type Like %?1%) and (?2 is null or t2.`name` Like %?2%) and (?3 is null or u1.id = ?3)",nativeQuery = true)
//    List<ToolFileEntity> findBytoolTypeAndByFileNameAndByUserId(String toolTpye, String fileName, String userId, Pageable pageable);

    List<ToolFileEntity> findByParentNodeAndToolEntity(ToolFileEntity parentNode, ToolEntity toolEntity);

    @Query(value = "SELECT t2.* from tool_entity t1 INNER JOIN tool_file_entity t2 ON (t1.id = t2.tool_entity_id) WHERE (?1 is null or t1.id = ?1)", countQuery = "SELECT t2.* from tool_entity t1 INNER JOIN tool_file_entity t2 ON (t1.id = t2.tool_entity_id) WHERE (?1 is null or t1.id = ?1)", nativeQuery = true)
    List<ToolFileEntity> findByToolId(String toolId, Pageable pageable);

//    @Query(value = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_file_entity t2 ON t1.id = t2.tool_entity_id WHERE u1.id = ?1 order by t2.create_time desc ", countQuery = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_file_entity t2 ON t1.id = t2.tool_entity_id WHERE u1.id = ?1 order by t2.create_time desc", nativeQuery = true)
//    List<ToolFileEntity> findByUserId(String userId);

    boolean existsByFileEntity(FileEntity fileEntity);
}
