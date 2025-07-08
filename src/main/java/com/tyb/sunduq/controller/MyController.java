package com.tyb.sunduq.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

@RestController
public class MyController {

    @GetMapping("/home")
    public void redirectToUaePass(HttpServletResponse response) throws IOException {
        String clientId = "sandbox_stage";
        String redirectUri = "https://localhost:8000";
        String scope = "urn:uae:digitalid:profile:general";
        String acrValues = "urn:safelayer:tws:policies:authentication:level:low";
        String state = UUID.randomUUID().toString();

        String url = "https://stg-id.uaepass.ae/idshub/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&scope=" + URLEncoder.encode(scope, "UTF-8") +
                "&state=" + state +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&acr_values=" + URLEncoder.encode(acrValues, "UTF-8");

        response.sendRedirect(url);
    }

}
