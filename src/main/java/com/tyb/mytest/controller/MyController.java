package com.tyb.mytest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/greet")
    public String greet(){
        return "hi";
    }

    @GetMapping("/simple")
    public String simple(){
        return "simple";
    }
    @GetMapping("/code")
    public String code(){
        return "hello im tayyab khan";
    }
}
