package com.cg.mapper;

import com.cg.pojo.vo.LeavesTypeQueryVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LeavesTypeMapper {
    List<LeavesTypeQueryVo> query();
}
