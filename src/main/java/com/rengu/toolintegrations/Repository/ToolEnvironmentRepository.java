//package com.rengu.toolintegrations.Repository;
//
//import com.rengu.toolintegrations.Entity.ToolEnvironmentEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface ToolEnvironmentRepository extends JpaRepository<ToolEnvironmentEntity, String> {
//    boolean existsByNameAndTypeAndVersion(String name, String type, String version);
//
//    List<ToolEnvironmentEntity> findByToolFileEntity(String toolFileId);
//}
