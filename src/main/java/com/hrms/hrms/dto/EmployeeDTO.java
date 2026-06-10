package com.hrms.hrms.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeDTO {
    private Integer empId;
    private String empName;
    private String gender;
    private LocalDate birthDate;
    private Integer deptId;
    private String phone;
    private String address;
    private LocalDate hireDate;
    private String position;
    private String education;
}