package com.cg.controller;

import com.cg.pojo.vo.PositionQueryVo;
import com.cg.service.PositionService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 岗位管理控制器层
 */
@RestController
@RequestMapping("/position")
public class PositionController {
    @Autowired
    private PositionService positionService;

    @GetMapping("/query/{departmentId}")
    public Result<List<PositionQueryVo>> query(@PathVariable("departmentId") Integer departmentId){
        System.out.println("Department ID: " + departmentId);  // 调试用，检查 departmentId 是否正确传递
        List<PositionQueryVo> vo = positionService.query(departmentId);
        return Result.success(vo);
    }
}
