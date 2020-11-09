package com.rengu.toolintegrations.Repository;

import com.rengu.toolintegrations.Entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @program: OperationsManagementSuiteV3
 * @author: hanchangming
 * @create: 2018-08-22 17:01
 **/

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    //根据用户名模糊查询用户信息
    @Query(value = "SELECT * from user_entity WHERE (?1 is null or username like %?1%) and (?2 is null or if_deleted = ?2) Order By create_time Desc",countQuery = "SELECT * from user_entity WHERE (?1 is null or username like %?1%) and (?2 is null or if_deleted = ?2) Order By create_time Desc",nativeQuery = true)
    List<UserEntity> findAllByUserNameAndIfDeleted(String userName,boolean IfDeleted);

    Page<UserEntity> findAllByIfDeleted(boolean ifDeleted, Pageable pageable);
}
