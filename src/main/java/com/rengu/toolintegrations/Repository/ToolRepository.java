package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.ToolFileEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, String> {
    //通过工具名，版本号，类型判断工具是否存在
    boolean existsByNameAndVersionAndTypeAndDeletedAndUserEntity(String toolName, String version, String type, boolean deleted, UserEntity userEntity);

    //根据用户和是否删除查询工具
    List<ToolEntity> findByDeletedAndUserEntity(boolean deleted, UserEntity userEntity, Pageable pageable);

    //根据工具名称或文件名模糊查询
    @Query(value = "SELECT t1.* from user_entity u1 INNER JOIN tool_entity t1 ON u1.id = t1.user_entity_id WHERE (?1 is null or t1.`name` Like %?1%) and (?2 is null or t1.deleted =?2) and (?3 is null or u1.id = ?3)", countQuery = "SELECT t1.* from user_entity u1 INNER JOIN tool_entity t1 ON u1.id = t1.user_entity_id WHERE (?1 is null or t1.`name` Like %?1%) and (?2 is null or t1.deleted =?2) and (?3 is null or u1.id = ?3)", nativeQuery = true)
    List<ToolEntity> findAllBytoolTypeAndByNameAndByDeleted( String toolName, boolean deleted,String userId,Pageable pageable);

    //根据工具名称或文件名模糊查询
    @Query(value = "SELECT t1.* from tool_entity t1  WHERE  (?1 is null or t1.type Like %?1%) and (?2 is null or t1.`name` Like %?2%) and (?3 is null or t1.deleted = ?3)", countQuery = "SELECT t1.* from tool_entity t1  WHERE  (?1 is null or t1.type Like %?1%) and (?2 is null or t1.`name` Like %?2%) and (?3 is null or t1.deleted = ?3)", nativeQuery = true)
    List<ToolEntity> findBytoolTypeAndByNameAndByDeleted(String toolTpye, String name, boolean deleted,Pageable pageable);

    //通过用户名模糊查询库中所有工具（管理员）
    @Query(value = "SELECT * from tool_entity WHERE (?1 is null or toolName like %?1%)",countQuery = "SELECT * from tool_entity WHERE (?1 is null or toolName like %?1%)",nativeQuery = true)
    List<ToolEntity> findAllByToolName(String toolName);

    List<ToolEntity> findAllByDeleted(boolean deleted, Sort sort);

    List<ToolEntity> getToolByUserEntity(UserEntity userEntity);

    Integer countByUserEntity_Id(String userId);

    @Query(value = "SELECT DISTINCT type FROM tool_entity",nativeQuery = true)
    List<String> findDistinctByType();
}
