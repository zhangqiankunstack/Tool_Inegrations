package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.FileEntity;
import com.rengu.toolintegrations.Entity.ToolConsequenceEntity;
import com.rengu.toolintegrations.Entity.ToolConsequenceFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToolConsequenceFileRepository extends JpaRepository<ToolConsequenceFileEntity, String> {

    boolean existsByNameAndExtensionAndParentNodeAndToolConsequenceEntity(String name, String extension, ToolConsequenceFileEntity parentNode, ToolConsequenceEntity toolConsequenceEntity);

    Optional<ToolConsequenceFileEntity> findByNameAndExtensionAndParentNodeAndToolConsequenceEntity(String name, String extension, ToolConsequenceFileEntity parentNode, ToolConsequenceEntity toolConsequenceEntity);

    //TODO:可能参数顺序有问题
    List<ToolConsequenceFileEntity> findByParentNodeAndToolConsequenceEntity(ToolConsequenceFileEntity parentNode,ToolConsequenceEntity toolConsequenceEntity);

    boolean existsByFileEntity(FileEntity fileEntity);

    @Query(value = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_consequence_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_consequence_file_entity t2 ON t1.id = t2.tool_consequence_entity_id WHERE u1.id = ?1 ORDER BY t2.create_time DESC ", countQuery = "SELECT t2.* FROM user_entity u1 INNER JOIN tool_consequence_entity t1 on u1.id = t1.user_entity_id INNER JOIN tool_consequence_file_entity t2 ON t1.id = t2.tool_consequence_entity_id WHERE u1.id = ?1 ORDER BY t2.create_time DESC", nativeQuery = true)
    List<ToolConsequenceFileEntity> findAllByUserId(String userId);
}
