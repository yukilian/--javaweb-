package com.hrms.hrms.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalaryDTO {
    private Integer empId;
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
}