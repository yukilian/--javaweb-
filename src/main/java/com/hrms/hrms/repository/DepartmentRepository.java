package com.hrms.hrms.repository;

import com.hrms.hrms.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository//数据访问层组件
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}