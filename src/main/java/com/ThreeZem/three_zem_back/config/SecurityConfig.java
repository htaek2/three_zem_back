package com.ThreeZem.three_zem_back.config;

import com.ThreeZem.three_zem_back.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity  // 이게 있어야 스프링이 웹 보안 설정 클래스로 인식
@RequiredArgsConstructor  // 생성자를 통한 의존성 주입은 순환 참조 문제를 컴파일 단계에서 발견할 수 있다고 함. 굳
public class SecurityConfig {

    // 다른 사람이 만든 클래스는 이런식으로 Bean에 등록해서 DI함
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO csrf 보안 기능 잠시 멈춤. 배포 단계에서 삭제 필요.
        // csrf : 다른 사이트에서 내 사이트를 원격으로 조작하는 것을 방지.
        http.csrf((csrf) -> {
            csrf.disable();
        });

        /*
        // 세션 방법에서 csrf 사용하는 방법. 참고로 적음.
        http.csrf(csrf -> {
            csrf.csrfTokenRepository(csrfTokenRepository())
                    .ignoringRequestMatchers("/login");  // CSRF 보안 기능을 끌 페이지
        });
        */

        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);   // 세션 생성 x
        });

        http.addFilterBefore(new JwtFilter(), ExceptionTranslationFilter.class);  // Exception~~ 필터 전에 내 필터 실행

        http.authorizeHttpRequests((authorize) -> {
            // 모든 url에서 접근 허가
            authorize.requestMatchers("/**").permitAll();
        });

        return http.build();
    }

    /*
    @Bean
    // 세션 방법에서 csrf 사용하는 방법. 참고로 적음.
    //
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
     */
}