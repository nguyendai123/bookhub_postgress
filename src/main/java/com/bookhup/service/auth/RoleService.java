package com.bookhup.service.auth;

import com.bookhup.model.Role;

import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    Role assignPermissionToRole(Long roleId, Long permissionId);
    List<Role> getAllRoles();
}

