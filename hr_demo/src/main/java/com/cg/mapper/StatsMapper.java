package com.cg.mapper;

import com.cg.pojo.vo.StatsQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Month;
import java.util.List;

@Mapper
public interface StatsMapper {


    List<Integer> queryLeaves(Integer employeeId, Month mouth);

    Integer queryDaysWorked(Integer employeeId, Month mouth);

    Integer queryDaysMissed(Integer employeeId, Month mouth);

    Integer queryDepartmentId(Integer employeeId);

    List<Integer> queryEmployeeIds(Integer departmentId);

    Integer queryDays(Integer employeeId);

    List<Integer> queryLeavaDays(Integer employeeId);
}
