package com.vslc.service;

import com.vslc.model.User;

import java.util.List;
import java.util.Map;

public interface IUserService {

    List<User> search(Map<String, Object> param);

    List<User> find(Map<String, Object> param);

    User login(Map<String, Object> param);

    User findByUserID(Integer userID);

    Integer getCount(Map<String, Object> param);

    Integer add(User user);

    void update(User user);

    void delete(Integer userID);
}
