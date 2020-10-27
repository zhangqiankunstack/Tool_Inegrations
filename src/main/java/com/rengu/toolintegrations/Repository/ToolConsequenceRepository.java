package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolConsequenceEntity;
import com.rengu.toolintegrations.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolConsequenceRepository extends JpaRepository<ToolConsequenceEntity,String> {

    List<ToolConsequenceEntity> findAllByUserEntity(UserEntity userEntity);
}
