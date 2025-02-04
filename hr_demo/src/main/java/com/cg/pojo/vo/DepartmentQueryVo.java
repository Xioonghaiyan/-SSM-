package com.cg.pojo.vo;

public class DepartmentQueryVo {

    private Integer departmentId;  // 部门ID（主键，自增）
    private String departmentName;  // 部门名称

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
