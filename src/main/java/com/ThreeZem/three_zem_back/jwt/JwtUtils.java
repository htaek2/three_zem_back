package com.ThreeZem.three_zem_back.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    /// jwt 복호화 키
    @Value("${app.jwtSecret}")
    private String jwtSecret;
    private static SecretKey key;
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    /// jwt를 생성.
    public static String createJwt(Map<String, String> payload) {

        return Jwts.builder()
                .claims(payload)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(getExpirationMinute(600))
                .signWith(key)
                .compact();
    }

    /// JWT 파싱
    public static Claims extractToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /// 분을 입력하면 ms로 만료 시간을 설정
    public static Date getExpirationMinute(int minute) {
        Date time = new Date();
        time.setTime(time.getTime() + (1000 * 60L * minute));

        return time;
    }

}
