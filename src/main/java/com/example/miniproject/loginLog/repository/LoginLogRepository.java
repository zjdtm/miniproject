package com.example.miniproject.loginLog.repository;

import com.example.miniproject.loginLog.domain.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    LoginLog findByMemberId(Long memberId);

}
