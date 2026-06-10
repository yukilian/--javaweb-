package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.EmployeeDTO;
import com.hrms.hrms.service.EmployeeService;
import com.hrms.hrms.vo.EmployeeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // 查询所有职员（支持姓名搜索）
    @GetMapping
    public Result<List<EmployeeVO>> getAllEmployees(
            @RequestParam(required = false) String keyword) {
        try {
            return Result.success(employeeService.getAllEmployees(keyword));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 查询单个职员
    @GetMapping("/{empId}")
    public Result<EmployeeVO> getEmployeeById(@PathVariable Integer empId) {
        try {
            return Result.success(employeeService.getEmployeeById(empId));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 按部门查询职员
    @GetMapping("/dept/{deptId}")
    public Result<List<EmployeeVO>> getEmployeesByDept(
            @PathVariable Integer deptId,
            @RequestParam(required = false) String keyword) {
        try {
            return Result.success(employeeService.getEmployeesByDept(deptId, keyword));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 新增职员
    @PostMapping
    public Result<Void> addEmployee(@RequestBody EmployeeDTO dto) {
        try {
            employeeService.addEmployee(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 编辑职员
    @PutMapping("/{empId}")
    public Result<Void> updateEmployee(@PathVariable Integer empId,
                                       @RequestBody EmployeeDTO dto) {
        try {
            employeeService.updateEmployee(empId, dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除职员
    @DeleteMapping("/{empId}")
    public Result<Void> deleteEmployee(@PathVariable Integer empId) {
        try {
            employeeService.deleteEmployee(empId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}