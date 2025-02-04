package com.cg.pojo.vo;

import lombok.Data;

@Data
public class LoginVo {
    private Integer permissions ; //员工权限 0-最高权限，1-人事部门，2-其他部门，3-员工
    private String token;//token

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getPermissions() {
        return permissions;
    }

    public void setPermissions(Integer permissions) {
        this.permissions = permissions;
    }
}
