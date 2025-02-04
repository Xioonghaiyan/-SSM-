package com.cg.service.impl;

import com.cg.mapper.DepartmentMapper;
import com.cg.pojo.vo.DepartmentQueryVo;
import com.cg.service.DepartmentService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public List<DepartmentQueryVo> query() {
        log.info("查询所有部门");
        List<DepartmentQueryVo> vo = departmentMapper.query();
        System.out.println(vo);
        return vo;
    }

    @Override
    public DepartmentQueryVo queryOne(Integer employeeId) {
        return departmentMapper.queryOne(employeeId);
    }

    @Override
    @Transactional
    public int add(String departmentName) {
        int rol = departmentMapper.add(departmentName);
        return rol;
    }

    @Override
    @Transactional
    public int remove(Integer departmentId) {
        int count = departmentMapper.getEmployee(departmentId);
        if (count > 0) {
            return 0;
        }
        int rol = departmentMapper.remove(departmentId);
        return rol;
    }

}
