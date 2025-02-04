package com.cg.service.impl;

import com.cg.common.Constant;
import com.cg.mapper.UserMapper;
import com.cg.pojo.dto.LoginDto;
import com.cg.pojo.dto.RegisterDto;
import com.cg.pojo.dto.UpdateDto;
import com.cg.pojo.entity.Employee;
import com.cg.pojo.vo.LoginVo;
import com.cg.pojo.vo.QueryVo;
import com.cg.service.UserService;
import com.cg.util.JwtUtil;
import com.cg.util.Md5Util;
import com.cg.util.OSSClientUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户服务层
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OSSClientUtil ossClientUtil;

    @Override
    @Transactional
    public LoginVo login(LoginDto loginDto) {
        //解析密码
        String password = Md5Util.getMD5String(loginDto.getPassword());
        System.out.println(password);
        //设置密码
        loginDto.setPassword(password);
        LoginVo vo = userMapper.login(loginDto);
        //生成token
        String token = null;
        try {
            token = JwtUtil.createToken(loginDto.getEmployeeId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        vo.setToken(token);
        System.out.println(token+" "+vo.getPermissions());
        return vo;
    }

    @Transactional
    public int register(RegisterDto registerDto) {
        //设置默认密码，年龄，权限
        String password = Md5Util.getMD5String(Constant.password);
        Integer age = Constant.age;
        Integer permissions = Constant.Permissions_low;

        Employee employee = new Employee();
        employee.setEmployeeId(registerDto.getEmployeeId());
        employee.setName(registerDto.getName());
        employee.setPassword(password);
        employee.setAge(age);
        employee.setDepartmentId(registerDto.getDepartmentId());
        employee.setPositionId(registerDto.getPositionId());
        employee.setPermissions(permissions);
        System.out.println(employee.getPermissions());

        int rol = userMapper.register(employee);
        return rol;
    }

    @Override
    public void uploadPic(MultipartFile file,Integer departmentId) {
        //  上传头像到 OSS
        String avatarUrl = null;
        if (file!= null &&!file.isEmpty()) {
            try {
                avatarUrl = ossClientUtil.uploadFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    @Transactional
    public List<QueryVo> query() {
        List<QueryVo> vo = userMapper.query();
        System.out.println(vo);
        return vo;
    }

    @Override
    @Transactional
    public List<QueryVo> queryByDepartmentId(int employeeId) {
        List<QueryVo> vo = userMapper.queryByDepartmentId(employeeId);
        return vo;
    }

    public QueryVo queryById(int employeeId) {
        QueryVo vo = userMapper.queryById(employeeId);
        return vo;
    }

    @Override
    @Transactional
    public int update(UpdateDto updateDto) {
        int rol = userMapper.update(updateDto);
        return rol;
    }

    @Override
    @Transactional
    public int delete(int employeeId) {
        int rol = userMapper.delete(employeeId);
        return rol;
    }

    @Override
    public List<QueryVo> searchEmployees(Integer employeeId, Integer departmentId) {
        return userMapper.searchEmployees(employeeId, departmentId);
    }
}
