package com.cg.service;

import com.cg.pojo.vo.PositionQueryVo;

import java.util.List;

public interface PositionService {
    List<PositionQueryVo> query(Integer departmentId);
}
