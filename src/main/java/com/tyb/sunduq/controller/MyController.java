package com.tyb.sunduq.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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

    @GetMapping("/")
    public void redirectToUAEPass(HttpServletResponse response) throws IOException {
        String redirectUrl = "https://stg-id.uaepass.ae/idshub/authorize" +
                "?response_type=code" +
                "&client_id=sandbox_stage" +
                "&scope=urn:uae:digitalid:profile:general" +
                "&state=HnlHOJTkTb66Y5H" +
                "&redirect_uri=https://localhost:8000" +
                "&acr_values=urn:safelayer:tws:policies:authentication:level:low";

        response.sendRedirect(redirectUrl);
    }
}
