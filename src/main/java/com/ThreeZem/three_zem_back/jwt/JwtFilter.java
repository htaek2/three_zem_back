package com.ThreeZem.three_zem_back.jwt;

import com.ThreeZem.three_zem_back.data.common.CustomUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

// OncePerRequestFilter : 각 API 요청에 따라 단일 실행을 보장함.
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request 안에는 유저의 여러 정보가 포함됨
        // response 로 유저에게 어떤 행동들을 시킬 수 있음(페이지 이동, 쿠키 생성 등)

        Cookie[] cookies = request.getCookies();

        // 쿠키 확인
        if (cookies == null) {
            logger.error("[Error] 쿠키 없음");
            filterChain.doFilter(request, response);
            return;
        }

        String jwtCookie = "";
        for (Cookie cookie : cookies){
            if (cookie.getName().equals("jwt")) {
                jwtCookie = cookie.getValue();
            }
        }

        // jwt 쿠키 확인
        if (jwtCookie.isEmpty()){
            logger.error("[Error] jwt 쿠키 없음");
            filterChain.doFilter(request, response);
            return;
        }

        // jwt 검증
        Claims claims = null;
        try {
            claims = JwtUtils.extractToken(jwtCookie);
            System.out.println(claims);
        }
        catch (Exception e) {
            logger.error("[Error] jwt 검증 실패 {}", String.valueOf(e));
            filterChain.doFilter(request, response);
            return;
        }

        // 문제 없으면 auth 변수에 유저 정보 넣기
        if (claims != null) {
            // auth 변수에 넣을 정보 생성
            var authorityArray = claims.get("authorities").toString().split(",");
            var authorities = Arrays.stream(authorityArray).map(SimpleGrantedAuthority::new).toList();

            var customUser = new CustomUser(claims.get("memberEmail").toString(), "none", authorities);  // password는 모르니 저장 안함
            customUser.setUserDisplayName(claims.get("memberName").toString());
            customUser.setBuildingId(claims.get("buildingId").toString());

            var authToken = new UsernamePasswordAuthenticationToken(
                    claims.get("memberEmail").toString(),
                    null,
                    authorities
            );
            // auth 변수의 detail 항목 채우기
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // auth 변수 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        else {
            logger.error("[Error] jwt 쿠키 데이터 오류");
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

}
