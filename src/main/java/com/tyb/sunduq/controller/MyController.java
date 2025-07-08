package com.tyb.sunduq.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MyController {

        @GetMapping("/")
        public String home(@AuthenticationPrincipal OAuth2User principal) {
            return "Welcome, " + principal.getAttribute("name");
        }

        @GetMapping("/login")
        public String login() {
            return "<a href=\"/oauth2/authorization/uaepass\">Login with UAEPass</a>";
        }
    }
