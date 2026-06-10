package com.hrms.hrms.repository;

import com.hrms.hrms.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByUsernameAndPassword(String username, String password);
    Optional<UserAccount> findByEmpId(Integer empId);
    void deleteByEmpId(Integer empId);
}