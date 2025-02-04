package com.cg.service.impl;

import com.cg.mapper.PositionMapper;
import com.cg.pojo.vo.PositionQueryVo;
import com.cg.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {
    private static final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);
    @Autowired
    private PositionMapper positionMapper;

    @Override
    public List<PositionQueryVo> query(Integer departmentId) {
        System.out.println("Department ID: " + departmentId);  // 调试用，检查 departmentId 是否正确传递
        List<PositionQueryVo> vo = positionMapper.query(departmentId);
        return vo;
     }
}
