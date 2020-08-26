package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity,String> {
    //通过工具名，版本号，类型判断工具是否存在
    boolean existsByNameAndVersionAndType(String toolName, String version, String type);

}
