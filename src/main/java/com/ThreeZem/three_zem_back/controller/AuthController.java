package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.data.dto.auth.LoginRequestDto;
import com.ThreeZem.three_zem_back.data.dto.auth.SignupRequestDto;
import com.ThreeZem.three_zem_back.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /// 회원가입
    @PostMapping("/api/auth/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto signupRequest) {
        return authService.register(signupRequest);
    }

    /// 로그인
    /// 서버는 세션을 사용하지 않는 상태 비저장(Stateless) 방식으로 동작한다.
    @PostMapping("/api/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        return authService.login(loginRequest, response);
    }

    /// 로그아웃
    /// 사용자로 부터 로그아웃 요청을 받으면 성공여부를 전송하고
    /// 성공시 사용자는 자체적으로 토큰을 제거한다.
    @PostMapping("/api/auth/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 승인. 클라이언트에서 토큰을 제거 바랍니다.");
    }
}