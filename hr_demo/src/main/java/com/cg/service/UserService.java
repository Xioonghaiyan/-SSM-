package com.cg.service;

import com.cg.pojo.dto.LoginDto;
import com.cg.pojo.dto.RegisterDto;
import com.cg.pojo.dto.UpdateDto;
import com.cg.pojo.vo.LoginVo;
import com.cg.pojo.vo.QueryVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    LoginVo login(LoginDto loginDto);

    int register(RegisterDto registerDto);

    List<QueryVo> query();

    List<QueryVo> queryByDepartmentId(int employeeId);

    QueryVo queryById(int employeeId);

    int update(UpdateDto updateDto);

    int delete(int employeeId);

    void uploadPic(MultipartFile file,Integer departmentId);

    /**
     * 根据员工ID或部门ID查询员工
     * @param employeeId 员工ID
     * @param departmentId 部门ID
     * @return 查询结果列表
     */
    List<QueryVo> searchEmployees(Integer employeeId, Integer departmentId);
}
