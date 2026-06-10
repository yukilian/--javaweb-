package com.hrms.hrms.controller;

import com.hrms.hrms.common.Result;
import com.hrms.hrms.dto.DepartmentDTO;
import com.hrms.hrms.entity.Department;
import com.hrms.hrms.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // 查询所有部门
    @GetMapping
    public Result<List<Department>> getAllDepartments() {
        try {
            return Result.success(departmentService.getAllDepartments());
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 查询单个部门
    @GetMapping("/{id}")
    public Result<Department> getDepartmentById(@PathVariable Integer id) {
        try {
            return Result.success(departmentService.getDepartmentById(id));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 新增部门
    @PostMapping
    public Result<Department> addDepartment(@RequestBody DepartmentDTO dto) {
        try {
            return Result.success(departmentService.addDepartment(dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 编辑部门
    @PutMapping("/{id}")
    public Result<Department> updateDepartment(@PathVariable Integer id,
                                               @RequestBody DepartmentDTO dto) {
        try {
            return Result.success(departmentService.updateDepartment(id, dto));
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 删除部门
    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Integer id) {
        try {
            departmentService.deleteDepartment(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}