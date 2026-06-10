package com.hrms.hrms.dto;

import lombok.Data;

@Data
public class DepartmentDTO {
    private String deptName;
    private String deptAddress;
    private String deptPhone;
    private Integer managerId;
}