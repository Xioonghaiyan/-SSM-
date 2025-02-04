package com.cg.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

public class AttendanceDto {
    private Integer employeeId;      // 员工ID
    private Integer departmentId;    // 部门ID
    private LocalDateTime workDate;       // 打卡时间
    private Integer status;          //打卡状态

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDateTime workDate) {
        this.workDate = workDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
