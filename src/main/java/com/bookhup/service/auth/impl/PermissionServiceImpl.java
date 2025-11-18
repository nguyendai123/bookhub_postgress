package com.bookhup.service.auth.impl;

import com.bookhup.model.Permission;
import com.bookhup.repository.PermissionRepository;
import com.bookhup.service.auth.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public Permission createPermission(Permission permission) {
        permission.setCreatedAt(java.time.LocalDateTime.now());
        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }
}

