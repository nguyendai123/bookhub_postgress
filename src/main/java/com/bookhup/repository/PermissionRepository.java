package com.bookhup.repository;

import com.bookhup.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String permissionName);

    @Query("""
                SELECT p FROM Permission p
                JOIN p.roles r
                JOIN r.users u
                WHERE u.userID = :userId
            """)
    Set<Permission> findPermissionsByUserId(Long userId);

}
