package com.cg.service.impl;

import com.cg.mapper.LeavesTypeMapper;
import com.cg.pojo.vo.LeavesTypeQueryVo;
import com.cg.service.LeavesTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeavesTypeServiceImpl implements LeavesTypeService {
    @Autowired
    private LeavesTypeMapper leavesTypeMapper;
    @Override
    public List<LeavesTypeQueryVo> query() {
        List<LeavesTypeQueryVo> vo = leavesTypeMapper.query();
        return vo;
    }


}
