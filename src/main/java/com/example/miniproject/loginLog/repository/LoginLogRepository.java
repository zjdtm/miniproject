package com.example.miniproject.loginLog.repository;

import com.example.miniproject.loginLog.domain.LoginLog;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Id> {

}
