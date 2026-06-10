package com.hrms.hrms.repository;

import com.hrms.hrms.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByEmpId(Integer empId);
    List<Attendance> findByEmpIdOrderByWorkDateDesc(Integer empId);
    List<Attendance> findByStatus(String status);
    List<Attendance> findByEmpIdAndStatus(Integer empId, String status);
    void deleteByEmpId(Integer empId);
}