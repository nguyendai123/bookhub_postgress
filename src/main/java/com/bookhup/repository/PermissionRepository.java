package com.bookhup.repository;

import com.bookhup.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Repository
@Transactional
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String permissionName);

    @Query("""
                SELECT p FROM Permission p
                JOIN p.roles r
                JOIN r.users u
                WHERE u.userId = :userId
            """)
    Set<Permission> findPermissionsByUserId(Long userId);

}
