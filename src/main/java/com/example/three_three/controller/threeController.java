package com.example.three_three.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class threeController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/overview")
    public String overview(){
        return "overview";
    }

    @GetMapping("/monitoring")
    public String page2(){
        return "monitoring";
    }

    @GetMapping("/condition")
    public String condition(){
        return "condition";
    }

    @GetMapping("/condition/detail")
    public String condition_detail(){
        return "condition_detail";
    }

    @GetMapping("/analysis")
    public String analysis(){
        return "analysis";
    }

    @GetMapping("/emission")
    public String emission(){
        return "emission";
    }

}
