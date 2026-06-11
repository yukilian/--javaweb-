package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.SalaryDTO;
import com.hrms.hrms.service.SalaryService;
import com.hrms.hrms.vo.SalaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    // 查询所有工资（支持姓名和月份筛选）
    @GetMapping
    public Result<List<SalaryVO>> getAllSalaries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer month) {
        try {
            return Result.success(salaryService.getAllSalaries(keyword, month));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 查询某个职员的工资
    @GetMapping("/emp/{empId}")
    public Result<List<SalaryVO>> getSalariesByEmp(
            @PathVariable Integer empId,
            @RequestParam(required = false) Integer month) {
        try {
            return Result.success(salaryService.getSalariesByEmp(empId, month));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 新增工资
    @PostMapping
    public Result<Void> addSalary(@RequestBody SalaryDTO dto) {
        try {
            salaryService.addSalary(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 编辑工资
    @PutMapping("/{salaryId}")
    public Result<Void> updateSalary(@PathVariable Integer salaryId,
                                     @RequestBody SalaryDTO dto) {
        try {
            salaryService.updateSalary(salaryId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除工资
    @DeleteMapping("/{salaryId}")
    public Result<Void> deleteSalary(@PathVariable Integer salaryId) {
        try {
            salaryService.deleteSalary(salaryId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}