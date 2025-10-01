package com.example.three_three.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.three_three.dto.LoginRequestDto;
import com.example.three_three.dto.SuccessResponse;
import com.example.three_three.dto.TokenResponseDto;
import com.example.three_three.service.AuthService;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    // 로그인 처리
    @PostMapping("/api/auth/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
    
    // 로그아웃 처리
    @PostMapping("/api/auth/logout")
    public ResponseEntity<SuccessResponse> logout() {
        return authService.logout();
    }
}
