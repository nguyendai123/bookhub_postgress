package com.bookhup.repository;

import com.bookhup.model.Role;
import com.bookhup.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleType roleName);

    @Query("""
                SELECT r FROM Role r
                JOIN r.users u
                WHERE u.userId = :userId
            """)
    Set<Role> findRolesByUserId(Long userId);

}
