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

    public void save(LoginLogDto.CreateLoginLog createLoginLog) {
        LoginLog loginLog = createLoginLog.toEntity();
        loginLogRepository.save(loginLog);
    }

}
