package com.hrms.hrms.repository;

import com.hrms.hrms.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findByEmpId(Integer empId);
    List<Salary> findByEmpIdAndSalaryMonth(Integer empId, Integer salaryMonth);
    List<Salary> findBySalaryMonth(Integer salaryMonth);
    List<Salary> findByEmpIdOrderBySalaryMonthDesc(Integer empId);
    void deleteByEmpId(Integer empId);
}