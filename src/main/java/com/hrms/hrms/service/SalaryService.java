package com.hrms.hrms.service;

import com.hrms.hrms.dto.SalaryDTO;
import com.hrms.hrms.vo.SalaryVO;
import java.util.List;

public interface SalaryService {
    List<SalaryVO> getAllSalaries(String keyword, Integer month);
    List<SalaryVO> getSalariesByEmp(Integer empId, Integer month);
    void addSalary(SalaryDTO dto);
    void updateSalary(Integer salaryId, SalaryDTO dto);
    void deleteSalary(Integer salaryId);
}