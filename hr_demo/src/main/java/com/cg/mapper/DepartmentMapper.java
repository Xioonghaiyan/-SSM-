package com.cg.mapper;

import com.cg.pojo.vo.DepartmentQueryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    List<DepartmentQueryVo> query();

    int add(String departmentName);

    int remove(Integer departmentId);

    int getEmployee(Integer departmentId);

    DepartmentQueryVo queryOne(Integer employeeId);

}
