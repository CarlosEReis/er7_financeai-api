package com.er7.financeai.utils;

import com.er7.financeai.common.Auth0TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
public class Auth0Utils {

    @Value("${com.er7.financeai.auth0.domain}") private String domain;
    @Value("${com.er7.financeai.auth0.clientId}") private String clientId;
    @Value("${com.er7.financeai.auth0.audience}") private String audience;
    @Value("${com.er7.financeai.auth0.clientSecret}") private String clientSecret;

    public String obterJwt() {

        var restTemplate = new RestTemplate();

        var request = new HashMap<String, String>();
        request.put("client_id", clientId);
        request.put("client_secret", clientSecret);
        request.put("audience", audience);
        request.put("grant_type", "client_credentials");

        var response = restTemplate.postForEntity(
            domain + "/oauth/token",
            request,
            Auth0TokenResponse.class
        );
        assert response.getBody() != null;
        return response.getBody().access_token();
    }

    private HttpHeaders getHeaderWithToken() {
        String token = obterJwt();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    public HttpEntity<Object> getEntityWithToken(Object body) {
        return new HttpEntity<>(body, getHeaderWithToken());
    }
}
