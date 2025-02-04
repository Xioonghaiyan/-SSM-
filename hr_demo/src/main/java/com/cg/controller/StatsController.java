package com.cg.controller;

import com.cg.pojo.vo.AttendanceVo;
import com.cg.pojo.vo.StatsQueryVo;
import com.cg.service.StatsService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考勤统计控制器层
 */
@RestController
@RequestMapping("/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    /**
     * 获取自己的考勤记录
     * @return
     */
    @GetMapping("/query/{employeeId}")
    public Result<StatsQueryVo> query(@PathVariable("employeeId") Integer employeeId){
        StatsQueryVo statsQueryVo = statsService.query(employeeId);
        return Result.success(statsQueryVo);
    }

    /**
     * 根据部门查询考勤记录
     * @param departmentId
     * @return
     */
    @GetMapping("/queryByDepartmentId/{departmentId}")
    public Result<List<StatsQueryVo>> queryByDepartmentId(@PathVariable("departmentId") Integer departmentId){
        List<StatsQueryVo> vo = statsService.queryqueryByDepartmentId(departmentId);
        return Result.success(vo);
    }

}
