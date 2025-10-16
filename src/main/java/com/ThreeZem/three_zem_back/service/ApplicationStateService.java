package com.ThreeZem.three_zem_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class ApplicationStateService {

    private final AtomicBoolean dataGenerated = new AtomicBoolean(false);
    private final AtomicBoolean isLogin = new AtomicBoolean(false);

    public boolean getDataGenerated() {
        return dataGenerated.get();
    }
    
    public boolean getIsLogin() {
        return isLogin.get();
    }

    public void setDataGenerated(boolean isGenerated) {
        this.dataGenerated.set(isGenerated);
        if (isGenerated) {
            System.out.println("\n[STATE] 과거 데이터 생성이 완료됐습니다.");
        }
    }
    
    public void setIsLogin(boolean isLogin) {
        this.isLogin.set(isLogin);
        if (isLogin) {
            log.info("\n[STATE] 로그인 됐습니다.");
        }
    }
}
