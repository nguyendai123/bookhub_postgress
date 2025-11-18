package com.bookhup.service.auth;


import com.bookhup.model.Permission;

import java.util.List;

public interface PermissionService {
    Permission createPermission(Permission permission);
    List<Permission> getAllPermissions();
}

