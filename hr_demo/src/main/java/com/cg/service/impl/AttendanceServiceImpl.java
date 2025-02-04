package com.cg.service.impl;

import com.cg.common.Constant;
import com.cg.mapper.AttendanceMapper;
import com.cg.pojo.dto.AttendanceDto;
import com.cg.pojo.vo.AttendanceVo;
import com.cg.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceMapper attendanceMapper;

    @Override
    public int addAttendance(Integer employeeId) {
        LocalDateTime dateTime = LocalDateTime.now();
        Integer status = Constant.failed;//默认打卡失败
        //判断打卡时间，如果时间小于上午9点，则代表打卡成功
        if(dateTime.getHour() < 9) {
            status = Constant.success;//打卡成功
        }
        Integer departmentId = attendanceMapper.queryByEmployeeId(employeeId);
        AttendanceDto attendDto = new AttendanceDto();
        attendDto.setEmployeeId(employeeId);
        attendDto.setDepartmentId(departmentId);
        attendDto.setWorkDate(dateTime);
        attendDto.setStatus(status);

        int rol = attendanceMapper.add(attendDto);
        return rol;
    }


}
