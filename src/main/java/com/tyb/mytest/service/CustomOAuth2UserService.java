package com.tyb.mytest.service;

import com.tyb.mytest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        processOAuth2User(userRequest, oauth2User);
        
        return oauth2User;
    }

    private void processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        User user = createUserFromOAuth2Attributes(registrationId, attributes);
        userService.saveOrUpdateUser(user);
    }

    private User createUserFromOAuth2Attributes(String registrationId, Map<String, Object> attributes) {
        User user = new User();
        
        if ("google".equals(registrationId)) {
            user.setEmail((String) attributes.get("email"));
            user.setName((String) attributes.get("name"));
            user.setGoogleId((String) attributes.get("sub"));
            user.setPicture((String) attributes.get("picture"));
            user.setEmailVerified((Boolean) attributes.get("email_verified"));
        }
        
        user.setProvider(registrationId);
        user.setEnabled(true);
        
        return user;
    }
}