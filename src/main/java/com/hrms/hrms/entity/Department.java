package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeptID")
    private Integer deptId;

    @Column(name = "DeptName")
    private String deptName;

    @Column(name = "DeptAddress")
    private String deptAddress;

    @Column(name = "DeptPhone")
    private String deptPhone;

    @Column(name = "ManagerID")
    private Integer managerId;
}