package com.hrms.hrms.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryVO {
    private Integer salaryId;
    private Integer empId;
    private String empName;
    private String deptName;
    private Integer salaryMonth;
    private BigDecimal baseSalary;
    private BigDecimal allowance;
    private BigDecimal tax;
    private BigDecimal insurance;
    private BigDecimal housingFund;
    private BigDecimal waterFee;
    private BigDecimal gasFee;
    private BigDecimal rentFee;
    private BigDecimal unionFee;
    private BigDecimal netSalary;  // 实发工资（计算得出）
}