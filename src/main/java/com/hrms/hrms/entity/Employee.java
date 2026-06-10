package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @Column(name = "EmpID")
    private Integer empId;

    @Column(name = "EmpName")
    private String empName;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "BirthDate")
    private LocalDate birthDate;

    @Column(name = "DeptID")
    private Integer deptId;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Address")
    private String address;

    @Column(name = "HireDate")
    private LocalDate hireDate;

    @Column(name = "Position")
    private String position;

    @Column(name = "Education")
    private String education;

    @Column(name = "AwardPenalty")
    private String awardPenalty;
}