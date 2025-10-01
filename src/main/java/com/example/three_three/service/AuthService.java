package com.example.three_three.service;

import com.example.three_three.dto.LoginRequestDto;
import com.example.three_three.dto.SuccessResponse;
import com.example.three_three.dto.TokenResponseDto;
import com.example.three_three.entity.Member;
import com.example.three_three.jwt.JwtUtil;
import com.example.three_three.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public ResponseEntity<TokenResponseDto> login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        Member member = memberRepository.findByEmailId(email).orElseThrow(
                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
        );

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(member.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

        return new ResponseEntity<>(new TokenResponseDto(token), headers, HttpStatus.OK);
    }

    public ResponseEntity<SuccessResponse> logout() {
        // In a stateless JWT implementation, logout is typically handled on the client-side.
        // The server can't effectively invalidate the token unless it maintains a blacklist.
        // For this implementation, we'll return a success response, assuming the client discards the token.
        return ResponseEntity.ok(new SuccessResponse("Logged out successfully"));
    }
}
