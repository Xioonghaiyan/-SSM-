package com.cg.mapper;

import com.cg.pojo.dto.LoginDto;
import com.cg.pojo.dto.RegisterDto;
import com.cg.pojo.dto.UpdateDto;
import com.cg.pojo.entity.Employee;
import com.cg.pojo.vo.LoginVo;
import com.cg.pojo.vo.QueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

@Mapper
public interface UserMapper {

    LoginVo login(LoginDto loginDto);

    int register(Employee employee);

    List<QueryVo> query();

    List<QueryVo> queryByDepartmentId(int employeeId);

    QueryVo queryById(int employeeId);

    int update(UpdateDto updateDto);

    int delete(int employeeId);

    /**
     * 根据员工ID或部门ID查询员工
     * @param employeeId 员工ID
     * @param departmentId 部门ID
     * @return 查询结果列表
     */
    List<QueryVo> searchEmployees(@Param("employeeId") Integer employeeId, 
                                 @Param("departmentId") Integer departmentId);

}
