package com.cg.controller;

import com.cg.pojo.vo.DepartmentQueryVo;
import com.cg.service.DepartmentService;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器层
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 查询所有部门
     * @return
     */
    @GetMapping("/query")
    public Result<List<DepartmentQueryVo>> query(){
        return Result.success(departmentService.query());
    }

    /**
     * 根据员工id查询部门
     * @param employeeId
     * @return
     */
    @GetMapping("/queryOne/{employeeId}")
    public Result<DepartmentQueryVo> queryOne(@PathVariable("employeeId") Integer employeeId){
        DepartmentQueryVo vo  = departmentService.queryOne(employeeId);
        return Result.success(vo);
    }

    /**
     * 新增部门
     * @param departmentName
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestParam("departmentName") String departmentName){
        int rol = departmentService.add(departmentName);
        if(rol < 1){
            return Result.error("添加部门失败");
        }
        return Result.success();
    }

    /**
     * 删除部门
     * @param departmentId
     * @return
     */
    @DeleteMapping("remove")
    public Result remove(@RequestParam("departmentId") Integer departmentId){
        int rol = departmentService.remove(departmentId);
        if(rol < 1){
            return Result.error("删除部门失败");
        }
        return Result.success();
    }

}
