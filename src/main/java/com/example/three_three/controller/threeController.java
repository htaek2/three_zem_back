package com.example.three_three.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class threeController {
    @GetMapping("/login")
    public String login(){
        return "page/login";
    }

    @GetMapping("/overview")
    public String overview(){
        return "page/overview";
    }

    @GetMapping("/monitoring")
    public String page2(){
        return "page/monitoring";
    }

    @GetMapping("/condition")
    public String condition(){
        return "page/condition";
    }

    @GetMapping("/condition/detail")
    public String condition_detail(){
        return "page/condition_detail";
    }

    @GetMapping("/analysis")
    public String analysis(){
        return "page/analysis";
    }

    @GetMapping("/emission")
    public String emission(){
        return "page/emission";
    }

}


