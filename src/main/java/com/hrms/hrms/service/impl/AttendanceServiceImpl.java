package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.AttendanceDTO;
import com.hrms.hrms.entity.Attendance;
import com.hrms.hrms.repository.AttendanceRepository;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.service.AttendanceService;
import com.hrms.hrms.vo.AttendanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private AttendanceVO toVO(Attendance a) {
        AttendanceVO vo = new AttendanceVO();
        vo.setAttendanceId(a.getAttendanceId());
        vo.setEmpId(a.getEmpId());
        vo.setWorkDate(a.getWorkDate());
        vo.setType(a.getType());
        vo.setCheckInTime(a.getCheckInTime());
        vo.setCheckOutTime(a.getCheckOutTime());
        vo.setDuration(a.getDuration());
        vo.setReason(a.getReason());
        vo.setStatus(a.getStatus() != null ? a.getStatus() : "待审核");

        // 查询职员姓名和部门
        employeeRepository.findById(a.getEmpId()).ifPresent(emp -> {
            vo.setEmpName(emp.getEmpName());
            if (emp.getDeptId() != null) {
                departmentRepository.findById(emp.getDeptId())
                        .ifPresent(dept -> vo.setDeptName(dept.getDeptName()));
            }
        });
        return vo;
    }

    @Override
    public List<AttendanceVO> getAllAttendances(String keyword, String status) {
        List<Attendance> list;
        if (status != null && !status.isEmpty() && !status.equals("全部状态")) {
            list = attendanceRepository.findByStatus(status);
        } else {
            list = attendanceRepository.findAll();
        }
        return list.stream()
                .map(this::toVO)
                .filter(vo -> keyword == null || keyword.isEmpty()
                        || (vo.getEmpName() != null && vo.getEmpName().contains(keyword)))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceVO> getAttendancesByEmp(Integer empId) {
        return attendanceRepository.findByEmpIdOrderByWorkDateDesc(empId)
                .stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void addAttendance(AttendanceDTO dto) {
        Attendance a = new Attendance();
        a.setEmpId(dto.getEmpId());
        a.setWorkDate(dto.getWorkDate());
        a.setType(dto.getType());
        a.setCheckInTime(dto.getCheckInTime());
        a.setCheckOutTime(dto.getCheckOutTime());
        a.setReason(dto.getReason());
        a.setStatus("待审核");

        // 计算时长
        if (dto.getCheckInTime() != null && dto.getCheckOutTime() != null) {
            long seconds = java.time.Duration.between(
                    dto.getCheckInTime(), dto.getCheckOutTime()).getSeconds();
            a.setDuration((float) seconds / 3600);
        }
        attendanceRepository.save(a);
    }

    @Override
    public void updateAttendanceStatus(Integer attendanceId, String status) {
        Attendance a = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("考勤记录不存在"));
        a.setStatus(status);
        attendanceRepository.save(a);
    }

    @Override
    public void deleteAttendance(Integer attendanceId) {
        if (!attendanceRepository.existsById(attendanceId)) {
            throw new RuntimeException("考勤记录不存在");
        }
        attendanceRepository.deleteById(attendanceId);
    }
}