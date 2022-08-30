package com.vslc.service.impl;

import com.vslc.dao.IUserDao;
import com.vslc.model.User;
import com.vslc.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "userService")
public class UserService implements IUserService {

    @Resource
    private IUserDao userDao;

    @Override
    public List<User> search(Map<String, Object> param) {
        return userDao.search(param);
    }

    @Override
    public List<User> find(Map<String, Object> param) {
        return userDao.find(param);
    }

    @Override
    public User login(Map<String, Object> param) {
        return userDao.login(param);
    }

    @Override
    public User findByUserID(Integer userID) {
        return userDao.findByUserID(userID);
    }

    @Override
    public Integer getCount(Map<String, Object> param) {
        return userDao.getCount(param);
    }

    @Override
    public Integer add(User user) {
        if (user.getCardID().equals("")) user.setCardID(null);
        if (user.getAddress().equals("")) user.setAddress(null);
        if (user.getPostCode().equals("")) user.setPostCode(null);
        if (user.getTitle().equals("")) user.setTitle(null);
        if (user.getTel().equals("")) user.setTel(null);
        if (user.getFax().equals("")) user.setFax(null);
        if (user.getEmail().equals("")) user.setEmail(null);
        return userDao.add(user);
    }

    @Override
    public void update(User user) {
        if (user.getCardID().equals("")) user.setCardID(null);
        if (user.getAddress().equals("")) user.setAddress(null);
        if (user.getPostCode().equals("")) user.setPostCode(null);
        if (user.getTitle().equals("")) user.setTitle(null);
        if (user.getTel().equals("")) user.setTel(null);
        if (user.getFax().equals("")) user.setFax(null);
        if (user.getEmail().equals("")) user.setEmail(null);
        userDao.update(user);
    }

    @Override
    public void delete(Integer userID) {
        userDao.delete(userID);
    }
}
