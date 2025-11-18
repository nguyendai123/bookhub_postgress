package com.bookhup.service.auth.impl;

import com.bookhup.model.Permission;
import com.bookhup.model.Role;
import com.bookhup.repository.PermissionRepository;
import com.bookhup.repository.RoleRepository;
import com.bookhup.service.auth.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public Role createRole(Role role) {
        role.setCreatedAt(java.time.LocalDateTime.now());
        return roleRepository.save(role);
    }

    @Override
    public Role assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        Permission permission = permissionRepository.findById(permissionId).orElseThrow();
        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

