package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.AttendanceDTO;
import com.hrms.hrms.service.AttendanceService;
import com.hrms.hrms.vo.AttendanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // 查询所有考勤（支持姓名和状态筛选）
    @GetMapping
    public Result<List<AttendanceVO>> getAllAttendances(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        try {
            return Result.success(attendanceService.getAllAttendances(keyword, status));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 查询某职员的考勤历史
    @GetMapping("/emp/{empId}")
    public Result<List<AttendanceVO>> getAttendancesByEmp(@PathVariable Integer empId) {
        try {
            return Result.success(attendanceService.getAttendancesByEmp(empId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 新增考勤
    @PostMapping
    public Result<Void> addAttendance(@RequestBody AttendanceDTO dto) {
        try {
            attendanceService.addAttendance(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 审核考勤（更新状态）
    @PutMapping("/{attendanceId}/status")
    public Result<Void> updateStatus(@PathVariable Integer attendanceId,
                                     @RequestBody Map<String, String> body) {
        try {
            attendanceService.updateAttendanceStatus(attendanceId, body.get("status"));
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除考勤
    @DeleteMapping("/{attendanceId}")
    public Result<Void> deleteAttendance(@PathVariable Integer attendanceId) {
        try {
            attendanceService.deleteAttendance(attendanceId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}