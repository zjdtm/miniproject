package com.example.miniproject.loginLog.service;

import com.example.miniproject.loginLog.domain.LoginLog;
import com.example.miniproject.loginLog.dto.LoginLogDto;
import com.example.miniproject.loginLog.repository.LoginLogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    // 로그인 로그 저장
    public void save(LoginLogDto.CreateLoginLog createLoginLog) {
        LoginLog loginLog = createLoginLog.toEntity();
        loginLogRepository.save(loginLog);
    }

    // 로그인 로그에 기록된 회원 찾기
    public LoginLog findLoginLog(Long userId) {
        LoginLog loginLog = loginLogRepository.findByMemberId(userId);
        return loginLog;
    }

}
