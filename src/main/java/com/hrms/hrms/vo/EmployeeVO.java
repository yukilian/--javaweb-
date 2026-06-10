package com.hrms.hrms.vo;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeVO {
    private Integer empId;
    private String empName;
    private String gender;
    private LocalDate birthDate;
    private Integer deptId;
    private String deptName;
    private String phone;
    private String address;
    private LocalDate hireDate;
    private String position;
    private String education;
}