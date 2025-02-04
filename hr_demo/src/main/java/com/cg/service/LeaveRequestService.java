package com.cg.service;

import com.cg.pojo.dto.LeaveRequestDto;
import com.cg.pojo.vo.LeaveRequestVo;

import java.util.List;

public interface LeaveRequestService {
    int add(LeaveRequestDto dto);

    List<LeaveRequestVo> query();

    void update(Integer id);

    List<LeaveRequestVo> queryOne(Integer employeeId);

    List<LeaveRequestVo> queryByDepartmentId(Integer departmentId);
}
