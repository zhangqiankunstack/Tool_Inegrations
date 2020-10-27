package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.FileEntity;
import com.rengu.toolintegrations.Entity.ToolEnvironmentFileEntity;
import com.rengu.toolintegrations.Entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolEnvironmentFileRepository extends JpaRepository<ToolEnvironmentFileEntity, String> {
    boolean existsByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolEnvironmentFileEntity parentNode, ToolEntity toolEntity);

    Optional<ToolEnvironmentFileEntity> findByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolEnvironmentFileEntity parentNode, ToolEntity toolEntity);

    List<ToolEnvironmentFileEntity> findByParentNodeAndToolEntity(ToolEnvironmentFileEntity parentNode, ToolEntity toolEntity);

    //List<ToolEnvironmentFileEntity> findByToolEntity(String ToolId);
    @Query(value = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_achievements_file t2 ON t1.id = t2.tool_entity_id WHERE u1.id = ?1 order by t2.create_time desc ", countQuery = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_achievements_file t2 ON t1.id = t2.tool_entity_id WHERE u1.id = ?1 order by t2.create_time desc", nativeQuery = true)
    List<ToolEnvironmentFileEntity> findByUserId(String userId);

    boolean existsByFileEntity(FileEntity fileEntity);
}
