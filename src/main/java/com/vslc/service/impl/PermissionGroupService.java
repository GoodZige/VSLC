package com.vslc.service.impl;

import com.vslc.dao.IPermissionGroupDao;
import com.vslc.model.PermissionGroup;
import com.vslc.service.IPermissionGroupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service(value = "permissionGroupService")
public class PermissionGroupService implements IPermissionGroupService {

    @Resource
    private IPermissionGroupDao permissionGroupDao;

    @Override
    public List<PermissionGroup> find() {
        return permissionGroupDao.find();
    }

    @Override
    public Integer getCount() {
        return permissionGroupDao.getCount();
    }

    @Override
    public Integer add(PermissionGroup permissionGroup) {
        return permissionGroupDao.add(permissionGroup);
    }

    @Override
    public void delete(int permissionGroupID) {
        permissionGroupDao.delete(permissionGroupID);
    }

    @Override
    public void update(PermissionGroup permissionGroup) {
        permissionGroupDao.update(permissionGroup);
    }

    @Override
    public PermissionGroup findByID(int permissionGroupID) {
        return permissionGroupDao.findByID(permissionGroupID);
    }
}
