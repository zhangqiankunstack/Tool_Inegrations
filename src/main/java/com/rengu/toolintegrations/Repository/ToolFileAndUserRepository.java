package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.ToolFileAndUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolFileAndUserRepository extends JpaRepository<ToolFileAndUser, String> {

    boolean existsByToolIdAndUserId(String toolId, String userId);

    void deleteByToolId(String id);
}
