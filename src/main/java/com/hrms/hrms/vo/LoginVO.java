package com.hrms.hrms.vo;

import lombok.Data;

@Data
public class LoginVO {
    private Integer userId;
    private String username;
    private String role;
    private Integer empId;
    private String empName;
    private Integer deptId;
    private Boolean isDeptManager;
}