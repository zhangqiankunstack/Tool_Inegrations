package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolAchievements;
import com.rengu.toolintegrations.Entity.ToolAchievementsFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ToolAchievementsFileRepository extends JpaRepository<ToolAchievementsFile,String> {
    boolean existsByNameAndExtensionAndParentNodeAndToolAchievements(String name, String extension, ToolAchievementsFile parentNode, ToolAchievements achievements);

    Optional<ToolAchievementsFile> findByNameAndExtensionAndParentNodeAndToolAchievements(String name, String extension, ToolAchievementsFile parentNode, ToolAchievements achievements);
}
