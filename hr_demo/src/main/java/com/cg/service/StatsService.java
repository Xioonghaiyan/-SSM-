package com.cg.service;

import com.cg.pojo.vo.AttendanceVo;
import com.cg.pojo.vo.StatsQueryVo;

import java.util.List;

public interface StatsService {
    StatsQueryVo query(Integer employeeId);

    List<StatsQueryVo> queryqueryByDepartmentId(Integer departmentId);

}
