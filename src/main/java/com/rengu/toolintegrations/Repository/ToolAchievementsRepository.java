package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolAchievements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolAchievementsRepository extends JpaRepository<ToolAchievements,String> {
    boolean existsByNameAndTypeAndVersion(String name,String type,String version);

    List<ToolAchievements> findByToolFileEntity(String toolFileId);
}
