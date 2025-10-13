package com.ThreeZem.three_zem_back.controller;

import com.ThreeZem.three_zem_back.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/// 테스트 용 화면 컨트롤러
@Controller
public class TestPageController {

    @GetMapping("/test")
    String loginPage(){
        return "login.html";
    }

    @GetMapping("/test/sse")
    String ssePage() {
        return "sse.html";
    }

}