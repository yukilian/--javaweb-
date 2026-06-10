package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttendanceID")
    private Integer attendanceId;

    @Column(name = "EmpID")
    private Integer empId;

    @Column(name = "WorkDate")
    private LocalDate workDate;

    @Column(name = "Type")
    private String type;

    @Column(name = "CheckInTime")
    private LocalTime checkInTime;

    @Column(name = "CheckOutTime")
    private LocalTime checkOutTime;

    @Column(name = "Duration")
    private Float duration;

    @Column(name = "Reason")
    private String reason;

    @Column(name = "Status")
    private String status;
}