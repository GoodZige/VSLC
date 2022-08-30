package com.vslc.dao;

import com.vslc.model.PermissionGroup;

import java.util.List;

public interface IPermissionGroupDao {

    PermissionGroup findByID(int permissionGroupID);

    List<PermissionGroup> find();

    Integer getCount();

    Integer add(PermissionGroup permissionGroup);

    void update(PermissionGroup permissionGroup);

    void delete(int permissionGroupID);
}
