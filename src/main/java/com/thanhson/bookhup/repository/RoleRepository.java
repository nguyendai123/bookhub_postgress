package com.thanhson.bookhup.repository;

import com.thanhson.bookhup.model.ERole;
import com.thanhson.bookhup.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
