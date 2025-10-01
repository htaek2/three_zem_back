package com.example.three_three.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String overview(Model model){
        model.addAttribute("isOverview",true);
        return "frame/frame";
    }

    @GetMapping("/monitoring")
    public String page2(Model model){
        model.addAttribute("isMonitoring",true);
        return "frame/frame";
    }

    @GetMapping("/condition")
    public String condition(Model model) {
        model.addAttribute("isCondition", true);
        return "frame/frame";
    }

    @GetMapping("/condition/detail")
    public String condition_detail(Model model){
        model.addAttribute("isConditionDetail",true);
        return "frame/frame";
    }

    @GetMapping("/analysis")
    public String analysis(Model model)
    {
        model.addAttribute("isAnalysis",true);
        return "frame/frame";
    }

    @GetMapping("/emission")
    public String emission(Model model)
    {
        model.addAttribute("isEmission",true);
        return "frame/frame";
    }

}


