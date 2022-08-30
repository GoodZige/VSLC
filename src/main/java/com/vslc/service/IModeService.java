package com.vslc.service;

import com.vslc.model.Mode;

import java.util.List;
import java.util.Map;

public interface IModeService {

    List<Mode> search(Map<String, Object> param);

    List<Mode> find();

    Integer getCount(String modeName);

    Integer add(Mode mode);

    void update(Mode mode);

    void delete(String modeID);
}
