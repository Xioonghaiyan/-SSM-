package com.cg.mapper;

import com.cg.pojo.vo.PositionQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PositionMapper {

    List<PositionQueryVo> query(@Param("departmentId") Integer departmentId);
}
