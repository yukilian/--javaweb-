package com.hrms.hrms.dto;

import lombok.Data;

@Data
public class SecurityDTO {
    private String oldAnswer;
    private String securityQuestion;
    private String newAnswer;
}