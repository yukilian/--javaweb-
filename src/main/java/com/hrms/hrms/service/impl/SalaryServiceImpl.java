package com.hrms.hrms.service.impl;

import com.hrms.hrms.dto.SalaryDTO;
import com.hrms.hrms.entity.Employee;
import com.hrms.hrms.entity.Salary;
import com.hrms.hrms.repository.DepartmentRepository;
import com.hrms.hrms.repository.EmployeeRepository;
import com.hrms.hrms.repository.SalaryRepository;
import com.hrms.hrms.service.SalaryService;
import com.hrms.hrms.vo.SalaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private SalaryVO toVO(Salary s) {
        SalaryVO vo = new SalaryVO();
        vo.setSalaryId(s.getSalaryId());
        vo.setEmpId(s.getEmpId());
        vo.setSalaryMonth(s.getSalaryMonth());
        vo.setBaseSalary(s.getBaseSalary());
        vo.setAllowance(s.getAllowance());
        vo.setTax(s.getTax());
        vo.setInsurance(s.getInsurance());
        vo.setHousingFund(s.getHousingFund());
        vo.setWaterFee(s.getWaterFee());
        vo.setGasFee(s.getGasFee());
        vo.setRentFee(s.getRentFee());
        vo.setUnionFee(s.getUnionFee());

        // 计算实发工资
        BigDecimal net = safe(s.getBaseSalary())
                .add(safe(s.getAllowance()))
                .subtract(safe(s.getTax()))
                .subtract(safe(s.getInsurance()))
                .subtract(safe(s.getHousingFund()))
                .subtract(safe(s.getWaterFee()))
                .subtract(safe(s.getGasFee()))
                .subtract(safe(s.getRentFee()))
                .subtract(safe(s.getUnionFee()));
        vo.setNetSalary(net);

        // 查询职员姓名和部门
        employeeRepository.findById(s.getEmpId()).ifPresent(emp -> {
            vo.setEmpName(emp.getEmpName());
            if (emp.getDeptId() != null) {
                departmentRepository.findById(emp.getDeptId())
                        .ifPresent(dept -> vo.setDeptName(dept.getDeptName()));
            }
        });
        return vo;
    }

    // 防止null值计算报错
    private BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    @Override
    public List<SalaryVO> getAllSalaries(String keyword, Integer month) {
        List<Salary> list;
        if (month != null) {
            list = salaryRepository.findBySalaryMonth(month);
        } else {
            list = salaryRepository.findAll();
        }
        // 关键词过滤（按姓名）
        return list.stream()
                .map(this::toVO)
                .filter(vo -> keyword == null || keyword.isEmpty()
                        || (vo.getEmpName() != null && vo.getEmpName().contains(keyword)))
                .collect(Collectors.toList());
    }

    @Override
    public List<SalaryVO> getSalariesByEmp(Integer empId, Integer month) {
        List<Salary> list = (month != null)
                ? salaryRepository.findByEmpIdAndSalaryMonth(empId, month)
                : salaryRepository.findByEmpIdOrderBySalaryMonthDesc(empId);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void addSalary(SalaryDTO dto) {
        Salary s = new Salary();
        s.setEmpId(dto.getEmpId());
        s.setSalaryMonth(dto.getSalaryMonth());
        s.setBaseSalary(dto.getBaseSalary());
        s.setAllowance(dto.getAllowance());
        s.setTax(dto.getTax());
        s.setInsurance(dto.getInsurance());
        s.setHousingFund(dto.getHousingFund());
        s.setWaterFee(dto.getWaterFee());
        s.setGasFee(dto.getGasFee());
        s.setRentFee(dto.getRentFee());
        s.setUnionFee(dto.getUnionFee());
        salaryRepository.save(s);
    }

    @Override
    public void updateSalary(Integer salaryId, SalaryDTO dto) {
        Salary s = salaryRepository.findById(salaryId)
                .orElseThrow(() -> new RuntimeException("工资记录不存在"));
        s.setSalaryMonth(dto.getSalaryMonth());
        s.setBaseSalary(dto.getBaseSalary());
        s.setAllowance(dto.getAllowance());
        s.setTax(dto.getTax());
        s.setInsurance(dto.getInsurance());
        s.setHousingFund(dto.getHousingFund());
        s.setWaterFee(dto.getWaterFee());
        s.setGasFee(dto.getGasFee());
        s.setRentFee(dto.getRentFee());
        s.setUnionFee(dto.getUnionFee());
        salaryRepository.save(s);
    }

    @Override
    public void deleteSalary(Integer salaryId) {
        if (!salaryRepository.existsById(salaryId)) {
            throw new RuntimeException("工资记录不存在");
        }
        salaryRepository.deleteById(salaryId);
    }
}