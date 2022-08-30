package com.vslc.dao;

import com.vslc.model.PathologicalType;

import java.util.List;
import java.util.Map;

/**
 * Created by chenlele
 * 2018/7/24 10:04
 */
public interface IPathologicalTypeDao {

    List<PathologicalType> search(Map<String, Object> param);

    List<PathologicalType> find();

    Integer getCount(String pathologicalTypeName);

    Integer add(PathologicalType pathologicalType);

    void update(PathologicalType pathologicalType);

    void delete(String pathologicalTypeID);
}
