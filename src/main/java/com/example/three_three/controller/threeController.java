package com.example.three_three.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class threeController {
    @GetMapping("/frame")
    public String frame() {
        return "frame/frame";
    }

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
    public String condition(Model model) {
        model.addAttribute("isCondition", true);
        return "frame/frame";
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


