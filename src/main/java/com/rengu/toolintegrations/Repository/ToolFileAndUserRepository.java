package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolFileAndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolFileAndUserRepository extends JpaRepository<ToolFileAndUser, String> {
    boolean existsByToolFileIdAndUserId(String toolFileId, String userId);
}
