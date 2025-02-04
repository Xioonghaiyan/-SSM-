package com.cg.mapper;

import com.cg.pojo.dto.AttendanceDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AttendanceMapper {


    int add(AttendanceDto attendDto);

    Integer queryByEmployeeId(Integer employeeId);

}
