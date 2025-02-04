package com.cg.service;

import com.cg.pojo.vo.DepartmentQueryVo;

import java.util.List;

public interface DepartmentService {
    List<DepartmentQueryVo> query();

    int add(String departmentName);

    int remove(Integer departmentId);

    DepartmentQueryVo queryOne(Integer employeeId);

}
