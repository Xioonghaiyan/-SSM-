package com.cg.mapper;

import com.cg.pojo.dto.LeaveRequestsDto;
import com.cg.pojo.vo.LeaveRequestVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LeaveRequestMapper {
    List<LeaveRequestVo> query();

    Integer querys(int employeeId);

    int add(LeaveRequestsDto vo);

    void update(Integer id, int approvalStatus);

    List<LeaveRequestVo> queryOne(Integer employeeId);

    List<LeaveRequestVo> queryByDepartmentId(Integer departmentId);
}
