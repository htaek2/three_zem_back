package com.ThreeZem.three_zem_back.service;

import com.ThreeZem.three_zem_back.config.BuildingDataCache;
import com.ThreeZem.three_zem_back.data.common.CustomUser;
import com.ThreeZem.three_zem_back.data.constant.ResponseMessage;
import com.ThreeZem.three_zem_back.data.dto.auth.LoginRequestDto;
import com.ThreeZem.three_zem_back.data.dto.auth.SignupRequestDto;
import com.ThreeZem.three_zem_back.data.entity.Building;
import com.ThreeZem.three_zem_back.data.entity.Member;
import com.ThreeZem.three_zem_back.jwt.JwtUtils;
import com.ThreeZem.three_zem_back.repository.BuildingRepository;
import com.ThreeZem.three_zem_back.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BuildingRepository buildingRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final BuildingDataCache buildingDataCache;
    private final ApplicationStateService applicationStateService;

    public ResponseEntity<String> login(LoginRequestDto loginRequest, HttpServletResponse response) {

        // 유저 정보 체크
        var authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);  // loadUserByUsername 메서드가 잘 설정되어 있다면 자동으로 유저 정보를 비교해줌.
        SecurityContextHolder.getContext().setAuthentication(auth);  // Jwt 사용해서 수동 로그인 방식으로 한다면 이 설정을 해야 다른 Controller에서 Authentication 변수 사용 가능

        CustomUser user = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 토큰 정보 추가
        String buildingId = String.valueOf(buildingDataCache.getBuildingDto().getId());

        Map<String, String> claims = new HashMap<>();
        claims.put("memberEmail", user.getUserEmail());
        claims.put("memberName", user.getUserDisplayName());
        claims.put("buildingId", buildingId);
        claims.put("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));

        String token = JwtUtils.createJwt(claims);

        // 토큰을 헤더에 넣어서 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        // 쿠키 생성
        var cookie = new Cookie("jwt", token);
        cookie.setMaxAge(36000);  // 쿠키 만료 시간. 초 단위. 10시간
        cookie.setHttpOnly(true);  // 쿠키 조작을 어렵게 만듬
        cookie.setPath("/");  // 어떤 페이지에서 생성할지
        response.addCookie(cookie);

        applicationStateService.setIsLogin(true);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(ResponseMessage.SUCCESS);
    }

    /// 회원가입
    public ResponseEntity<String> register(SignupRequestDto signupRequest) {

        // 등록된 email 확인
        Optional<Member> result = memberRepository.findByEmail(signupRequest.getEmail());

        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.CLIENT_ERROR);
        }

        // 유효한 빌딩 ID인지 확인
        Optional<Building> building = buildingRepository.findById(UUID.fromString(signupRequest.getBuildingId()));

        if (building.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.CLIENT_ERROR);
        }

        var hash = passwordEncoder.encode(signupRequest.getPassword());

        Member member = new Member();
        member.setUserName(signupRequest.getUsername());
        member.setEmail(signupRequest.getEmail());
        member.setPassword(hash);

        // DB에 저장
        memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseMessage.SUCCESS);
    }

    /// 로그아웃 처리
    public ResponseEntity<String> logout() {
        // 빌딩 데이터 제거
        buildingDataCache.clearData();
        applicationStateService.setIsLogin(false);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.SUCCESS);
    }
}
