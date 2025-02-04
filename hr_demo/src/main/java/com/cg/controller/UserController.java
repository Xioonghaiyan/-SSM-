package com.cg.controller;

import com.cg.pojo.dto.LoginDto;
import com.cg.pojo.dto.RegisterDto;
import com.cg.pojo.dto.UpdateDto;
import com.cg.pojo.vo.LoginVo;
import com.cg.pojo.vo.QueryVo;
import com.cg.service.UserService;
import com.cg.util.JwtUtil;
import com.cg.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户控制器层
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@RequestBody LoginDto loginDto) {
        LoginVo vo = userService.login(loginDto);
        return Result.success(vo);
    }

    /**
     * 注册用户
     * @param registerDto 注册信息
     * @return 包含用户ID的成功响应
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto registerDto) {
        int rol = userService.register(registerDto);
        if (rol < 1) {
            return Result.error("注册失败");
        }
        return Result.success();  // 返回新创建的用户ID
    }

    /**
     * 获取用户列表
     * @return
     */
    @GetMapping("/query")
    public Result<List<QueryVo>> query() {
        List<QueryVo> vo = userService.query();
        return Result.success(vo);
    }

    /**
     * 获取部门列表
     * @param employeeId
     * @return
     */
    @GetMapping("/query/{employeeId}")
    public Result<List<QueryVo>> queryByDepartmentId(@PathVariable int employeeId){
        List<QueryVo> vo = userService.queryByDepartmentId(employeeId);
        return Result.success(vo);
    }

    /**
     * 获取单个用户信息
     * @param employeeId
     * @return
     */
    @GetMapping("/queryId/{employeeId}")
    public Result<QueryVo> queryById(@PathVariable int employeeId){
        QueryVo vo = userService.queryById(employeeId);
        return Result.success(vo);
    }

    /**
     * 修改员工
     * @param updateDto
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody UpdateDto updateDto) {
        int rol = userService.update(updateDto);
        if (rol < 1) {
            return Result.error("修改失败");
        }
        return Result.success(); // 返回类型推断为 void
    }

    /**
     * 删除员工
     * @param employeeId
     * @return
     */
    @DeleteMapping("/delete/{employeeId}")
    public Result delete(@PathVariable int employeeId) {
        int rol = userService.delete(employeeId);
        if (rol < 1) {
            return Result.error("删除失败");
        }
        return Result.success(); // 返回类型推断为 void
    }

    /**
     * 根据员工ID或部门ID查询员工
     * @param employeeId 员工ID（可选）
     * @param departmentId 部门ID（可选）
     * @return 查询结果
     */
    @GetMapping("/search")
    public Result<List<QueryVo>> searchEmployees(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) Integer departmentId) {
        
        if (employeeId == null && departmentId == null) {
            return Result.error("员工ID和部门ID不能同时为空");
        }
        
        List<QueryVo> results = userService.searchEmployees(employeeId, departmentId);
        return Result.success(results);
    }
}
