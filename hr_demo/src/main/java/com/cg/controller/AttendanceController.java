package com.cg.controller;

import com.cg.pojo.dto.AttendanceDto;
import com.cg.pojo.vo.AttendanceVo;
import com.cg.service.AttendanceService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤管理控制器层
 */
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    /**
     * 新增考勤
     * @param employeeId
     * @return
     */
    @PostMapping("/add")
    public Result addAttendance(@RequestParam("employeeId") Integer employeeId){
        int rol = attendanceService.addAttendance(employeeId);
        if(rol < 1){
            return Result.error("添加考勤记录失败");
        }
        return Result.success();
    }




}
