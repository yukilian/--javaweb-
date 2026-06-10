package com.hrms.hrms.service;

import com.hrms.hrms.dto.AttendanceDTO;
import com.hrms.hrms.vo.AttendanceVO;
import java.util.List;

public interface AttendanceService {
    List<AttendanceVO> getAllAttendances(String keyword, String status);
    List<AttendanceVO> getAttendancesByEmp(Integer empId);
    void addAttendance(AttendanceDTO dto);
    void updateAttendanceStatus(Integer attendanceId, String status);
    void deleteAttendance(Integer attendanceId);
}