package com.vslc.service;

import com.vslc.model.PermissionGroup;

import java.util.List;

public interface IPermissionGroupService {

    List<PermissionGroup> find();

    Integer getCount();

    Integer add(PermissionGroup permissionGroup);

    void delete(int permissionGroupID);

    void update(PermissionGroup permissionGroup);

    PermissionGroup findByID(int permissionGroupID);
}
