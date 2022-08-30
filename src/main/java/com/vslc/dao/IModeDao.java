package com.vslc.dao;

import com.vslc.model.Mode;

import java.util.List;
import java.util.Map;

public interface IModeDao {

    List<Mode> search(Map<String, Object> param);

    Mode findByModeName(String modeName);

    List<Mode> find();

    Integer getCount(String modeName);

    Integer add(Mode mode);

    void update(Mode mode);

    void delete(String modeID);
}
