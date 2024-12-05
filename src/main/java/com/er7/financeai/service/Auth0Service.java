package com.er7.financeai.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

@Service
public class Auth0Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auth0Service.class);

    private RestClient client;

    @Value("${com.er7.financeai.auth0.domain}")
    private String auth0Domain;

    // TODO: tratar token expirado
    @Value("${com.er7.financeai.auth0.management.api.token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        this.client = RestClient.builder()
            .baseUrl(auth0Domain)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.accessToken)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            .build();
    }

    // TODO: tratar falha na atualizacao do usuario
    public void addRolePaidIn(String userId) {
        LOGGER.info("Atualizando usuario no Auth0 com a role PAID");
        var body = "{ \"roles\": [\"rol_WwiIorl4u6WjivLt\"] }";

        ResponseSpec responseSpec = client
                .post()
                .uri("/api/v2/users/{userId}/roles", userId)
                .body(body)
                .retrieve();

        ResponseEntity<Void> response = responseSpec.toBodilessEntity();
        if (HttpStatus.NO_CONTENT.equals(response.getStatusCode())) {
            LOGGER.info("Atualizacao do usuario Auth0 com a role PAID, realizada com sucesso.");
        } else {
            LOGGER.error("Nao foi possivel atualizar o usuario no Auth0 com a role PAID: {}", response.getBody());
        }

    }

}
