package com.cg.pojo.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Attendance {

    private Integer attendanceId;    // 考勤记录ID
    private Integer employeeId;      // 员工ID
    private Integer departmentId;    // 部门ID
    private LocalDateTime workDate;       // 打卡时间
    private Integer status;      //打卡状态
}

