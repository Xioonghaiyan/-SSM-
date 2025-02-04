package com.cg.controller;

import com.cg.pojo.vo.LeavesTypeQueryVo;
import com.cg.service.LeavesTypeService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 请假类别控制器层
 */
@RestController
@RequestMapping("/leavesType")
public class LeavesTypeController {
    @Autowired
    private LeavesTypeService leavesTypeService;

    /**
     * 查询所有请假类型
     * @return
     */
    @RequestMapping("/query")
    public Result<List<LeavesTypeQueryVo>> query(){
        List<LeavesTypeQueryVo> vo = leavesTypeService.query();
        return Result.success(vo);

    }



}
