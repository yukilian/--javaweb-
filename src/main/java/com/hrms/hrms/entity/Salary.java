package com.hrms.hrms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "salary")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SalaryID")
    private Integer salaryId;

    @Column(name = "EmpID")
    private Integer empId;

    @Column(name = "BaseSalary")
    private BigDecimal baseSalary;

    @Column(name = "Allowance")
    private BigDecimal allowance;

    @Column(name = "Tax")
    private BigDecimal tax;

    @Column(name = "Insurance")
    private BigDecimal insurance;

    @Column(name = "HousingFund")
    private BigDecimal housingFund;

    @Column(name = "SalaryMonth")
    private Integer salaryMonth;

    @Column(name = "WaterFee")
    private BigDecimal waterFee;

    @Column(name = "GasFee")
    private BigDecimal gasFee;

    @Column(name = "RentFee")
    private BigDecimal rentFee;

    @Column(name = "UnionFee")
    private BigDecimal unionFee;
}