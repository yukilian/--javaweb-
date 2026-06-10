package com.hrms.hrms.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceVO {
    private Integer attendanceId;
    private Integer empId;
    private String empName;
    private String deptName;
    private LocalDate workDate;
    private String type;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private Float duration;
    private String reason;
    private String status;
}