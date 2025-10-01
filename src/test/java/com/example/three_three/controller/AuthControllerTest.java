package com.example.three_three.controller;

import com.example.three_three.dto.LoginRequestDto;
import com.example.three_three.entity.Member;
import com.example.three_three.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(com.example.three_three.TestConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testUser;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        testUser = new Member();
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        memberRepository.save(testUser);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_wrong_password() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "wrongpassword");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_user_not_found() throws Exception {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("nonexistent@example.com", "password");

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isBadRequest());
    }
}
