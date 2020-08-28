package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity,String> {
    //通过工具名，版本号，类型判断工具是否存在
    boolean existsByNameAndVersionAndType(String toolName, String version, String type);

    //根据用户和是否删除查询工具
    List<ToolEntity> findByDeletedAndUserEntity(boolean deleted, UserEntity userEntity, Pageable pageable);
}
