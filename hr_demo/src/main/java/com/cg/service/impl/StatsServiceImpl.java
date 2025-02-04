package com.cg.service.impl;

import com.cg.mapper.StatsMapper;
import com.cg.pojo.vo.StatsQueryVo;
import com.cg.service.StatsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private StatsMapper statsMapper;

    @Override
    @Transactional
    public StatsQueryVo query(Integer employeeId) {
        //通过传递一个时间查询当月的记录
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime);
        Month mouth = dateTime.getMonth();
        List<Integer> leavesDays = statsMapper.queryLeaves(employeeId,mouth);

        Integer days = 0;
        for(Integer i : leavesDays){
            days += i;
        }
        Integer departmentId = statsMapper.queryDepartmentId(employeeId);
        Integer daysWorked = statsMapper.queryDaysWorked(employeeId,mouth);

        StatsQueryVo vo = new StatsQueryVo(employeeId,departmentId,days,daysWorked);
        System.out.println(employeeId);
        System.out.println(departmentId);
        System.out.println(days);
        System.out.println(daysWorked);
        return vo;
    }

    @Override
    public List<StatsQueryVo> queryqueryByDepartmentId(Integer departmentId) {
        //先查询部门员工
        List<Integer> employeeIds = statsMapper.queryEmployeeIds(departmentId);
        List<StatsQueryVo> vo = new ArrayList<>();
        for(Integer employeeId : employeeIds)
        {
            StatsQueryVo statsQueryVo = new StatsQueryVo();
            //一个月打卡成功天数
            Integer workDays = statsMapper.queryDays(employeeId);
            //一个月请假天数
            List<Integer> leavesDays = statsMapper.queryLeavaDays(employeeId);
            Integer leaveDays = 0;
            for(Integer i : leavesDays){
                leaveDays += i;
            }
            statsQueryVo.setEmployeeId(employeeId);
            statsQueryVo.setDepartmentId(departmentId);
            statsQueryVo.setDaysWorked(workDays);
            statsQueryVo.setLeaveDays(leaveDays);
            vo.add(statsQueryVo);
        }
        return vo;
    }

}
