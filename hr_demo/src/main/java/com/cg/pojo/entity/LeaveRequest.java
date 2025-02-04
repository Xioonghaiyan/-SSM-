package com.cg.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LeaveRequest {

    private int id;                // 请假请求ID
    private int employeeId;        // 员工ID
    private int departmentId;      // 部门ID
    private LocalDateTime leaveTime; //请假时间
    private int leaveDays;         // 请假天数
    private String typeId;         // 请假类型
    private String leaveReason;    // 请假原因
    private int approvalStatus;    // 审核状态

}

