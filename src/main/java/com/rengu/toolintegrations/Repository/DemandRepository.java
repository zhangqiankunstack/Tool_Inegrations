package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.DemandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandRepository extends JpaRepository<DemandEntity,String> {

    //通过工作流名称，类型，是否删除判断工具流是否存在
    boolean existsByNameAndTypeAndDeleted(String name, String type, boolean deleted);
}
