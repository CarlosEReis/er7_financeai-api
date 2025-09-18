package com.er7.financeai.common;

public record Auth0TokenResponse(String access_token, String token_type, Integer expires_in) {}
