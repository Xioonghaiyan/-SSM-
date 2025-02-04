package com.cg.controller;

import com.cg.pojo.dto.LeaveRequestDto;
import com.cg.pojo.vo.LeaveRequestVo;
import com.cg.service.LeaveRequestService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 请假管理控制器层
 */
@RestController
@RequestMapping("/leaveRequest")
public class LeaveRequestController {
    @Autowired
    private LeaveRequestService leaveRequestService;

    /**
     * 获取请假列表
     * @return
     */
    @GetMapping("/query")
    public Result<List<LeaveRequestVo>> query(){
        List<LeaveRequestVo> vo = leaveRequestService.query();
        return Result.success(vo);
    }

    /**
     * 获取单个员工的请假记录
     * @param employeeId
     * @return
     */
    @GetMapping("/queryOne/{employeeId}")
    public Result<List<LeaveRequestVo>> query(@PathVariable("employeeId") Integer employeeId){
        List<LeaveRequestVo> vo = leaveRequestService.queryOne(employeeId);
        return Result.success(vo);
    }

    /**
     * 根据部门查询请假记录
     * @param departmentId
     * @return
     */
    @GetMapping("/queryByDepartmentId/{departmentId}")
    public Result<List<LeaveRequestVo>> queryByDepartmentId(@PathVariable("departmentId") Integer departmentId){
        List<LeaveRequestVo> vo = leaveRequestService.queryByDepartmentId(departmentId);
        return Result.success(vo);
    }

    /**
     * 新增请假信息
     * @param dto
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody LeaveRequestDto dto) {
        int rol = leaveRequestService.add(dto);
        if (rol < 1) {
            return Result.error("添加请假申请失败");
        }
        return Result.success();
    }

    /**
     * 审核请假
     * @param id
     * @return
     */
    @PostMapping("/update")
    public Result update(Integer id){
        System.out.println(id);
        leaveRequestService.update(id);
        return Result.success();
    }
}
