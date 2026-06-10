package com.hrms.hrms.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceDTO {
    private Integer empId;
    private LocalDate workDate;
    private String type;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String reason;
}