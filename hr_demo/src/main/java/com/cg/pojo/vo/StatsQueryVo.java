package com.cg.pojo.vo;

public class StatsQueryVo {
    private Integer employeeId;      // 员工ID
    private Integer departmentId;    // 部门ID
    private Integer leaveDays;         // 请假天数
    private Integer daysWorked;      // 打卡成功天数

    public StatsQueryVo(Integer employeeId, Integer departmentId, int leaveDays, int daysWorked) {
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.leaveDays = leaveDays;
        this.daysWorked = daysWorked;
    }

    public StatsQueryVo(){}

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(Integer daysWorked) {
        this.daysWorked = daysWorked;
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }
}
