package com.vslc.service.impl;

import com.vslc.dao.IModeDao;
import com.vslc.model.Mode;
import com.vslc.service.IModeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service(value = "modeService")
public class ModeService implements IModeService {

    @Resource
    private IModeDao modeDao;

    @Override
    public List<Mode> search(Map<String, Object> param) {
        return modeDao.search(param);
    }

    @Override
    public List<Mode> find() {
        return modeDao.find();
    }

    @Override
    public Integer getCount(String modeName) {
        return modeDao.getCount(modeName);
    }

    @Override
    public Integer add(Mode mode) {
        return modeDao.add(mode);
    }

    @Override
    public void update(Mode mode) {
        modeDao.update(mode);
    }

    @Override
    public void delete(String modeID) {
        modeDao.delete(modeID);
    }
}
