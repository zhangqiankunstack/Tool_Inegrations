package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.ToolFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolFileRepository extends JpaRepository<ToolFileEntity, String> {

    boolean existsByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity);

    Optional<ToolFileEntity> findByNameAndExtensionAndParentNodeAndToolEntity(String name, String extension, ToolFileEntity parentNode, ToolEntity toolEntity);


}
