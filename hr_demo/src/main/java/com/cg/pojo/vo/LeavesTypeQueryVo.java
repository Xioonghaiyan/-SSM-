package com.cg.pojo.vo;

import lombok.Data;

@Data
public class LeavesTypeQueryVo {
    private Integer typeId; //类型编号
    private String type; //类型

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
