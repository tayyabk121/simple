package com.tyb.sunduq.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MyController {

        private final String clientId = "sandbox_stage";
        private final String redirectUri = "https://localhost:8000"; // Must be registered with UAE Pass
        private final String authorizationEndpoint = "https://stg-id.uaepass.ae/idshub/authorize";
        private final String scope = "urn:uae:digitalid:profile:general";
        private final String state = "HnlHOJTkTb66Y5H"; // You should generate a random state in production
        private final String acrValues = "urn:safelayer:tws:policies:authentication:level:low";

        @GetMapping("/")
        public void redirectToUaePass(HttpServletResponse response) throws IOException {
            String authUrl = authorizationEndpoint +
                    "?response_type=code" +
                    "&client_id=" + clientId +
                    "&scope=" + scope +
                    "&state=" + state +
                    "&redirect_uri=" + redirectUri +
                    "&acr_values=" + acrValues;

            response.sendRedirect(authUrl);
        }

        @GetMapping("/callback")
        public String handleCallback(
                @RequestParam(name = "code", required = false) String code,
                @RequestParam(name = "state", required = false) String state,
                @RequestParam(name = "error", required = false) String error
        ) {
            if (error != null) {
                return "Error: " + error;
            }
            // In production, you should validate 'state' and exchange 'code' for access token
            return "Authorization Code Received: " + code;
        }
    }
