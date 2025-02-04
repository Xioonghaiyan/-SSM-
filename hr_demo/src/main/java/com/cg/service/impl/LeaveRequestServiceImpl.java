package com.cg.service.impl;

import com.cg.common.Constant;
import com.cg.mapper.LeaveRequestMapper;
import com.cg.pojo.dto.LeaveRequestDto;
import com.cg.pojo.dto.LeaveRequestsDto;
import com.cg.pojo.vo.LeaveRequestVo;
import com.cg.service.LeaveRequestService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {
    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Override
    @Transactional
    public List<LeaveRequestVo> query() {
        List<LeaveRequestVo> list = leaveRequestMapper.query();
        return list;
    }

    @Override
    @Transactional
    public List<LeaveRequestVo> queryOne(Integer employeeId) {
        List<LeaveRequestVo> vo = leaveRequestMapper.queryOne(employeeId);
        return vo;
    }

    @Override
    public List<LeaveRequestVo> queryByDepartmentId(Integer departmentId) {
        List<LeaveRequestVo> vo = leaveRequestMapper.queryByDepartmentId(departmentId);
        return vo;
    }

    @Override
    @Transactional
    public int add(LeaveRequestDto dto) {
        Integer departmentId = leaveRequestMapper.querys(dto.getEmployeeId());

        LeaveRequestsDto vo = new LeaveRequestsDto();
        vo.setEmployeeId(dto.getEmployeeId());
        vo.setDepartmentId(departmentId);
        vo.setLeaveTime(dto.getLeaveTime());
        vo.setLeaveDays(dto.getLeaveDays());
        vo.setTypeId(dto.getTypeId());
        vo.setLeaveReason(dto.getLeaveReason());
        Integer status = Constant.Leaves_Review;//默认未审核
        vo.setApprovalStatus(status);
        return leaveRequestMapper.add(vo);
    }

    @Override
    @Transactional
    public void update(Integer id) {
        //修改审核状态为通过
        int approvalStatus = Constant.Leaves_Pass;
        leaveRequestMapper.update(id, approvalStatus);
    }
}
