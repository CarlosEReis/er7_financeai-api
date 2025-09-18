package com.er7.financeai.domain.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec;

import java.util.Optional;

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

    public void removeRolePaidIn(String userId) {
        LOGGER.info("Removendo a role PAID do usuario {} no Auth0", userId);
        var body = "{ \"roles\": [\"rol_WwiIorl4u6WjivLt\"] }";

        ResponseEntity<Void> response = client
            .method(HttpMethod.DELETE)
            .uri("/api/v2/users/{userId}/roles", userId)
            .body(body)
            .retrieve()
            .toBodilessEntity();

        if (HttpStatus.NO_CONTENT.equals(response.getStatusCode())) {
            LOGGER.info("Role PAID, removida com sucesso.");
        } else {
            LOGGER.error("Nao foi possivel remover a role PAID: {}", response.getBody());
        }
    }

    public void addMetaData(String userId, String subscriptionId) {
        String body = "{\"user_metadata\":{},\"app_metadata\":{ \"stripe_subscription\":\"" + subscriptionId + "\"}}";
        LOGGER.info("ENVIANDO JSON: {}.", body);
         ResponseSpec responseSpec = client
            .patch()
            .uri("/api/v2/users/{userId}", userId)
            .body(body)
            .retrieve();

        ResponseEntity<Void> response = responseSpec.toBodilessEntity();
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            LOGGER.info("Atualizacao do usuario Auth0 com o subscriptionId da Stripe, realizada com sucesso.");
        } else {
            LOGGER.error("Nao foi possivel atualizar o usuario no Auth0 o subscriptionId da Stripe: {}", response.getBody());
        }
    }

    public Optional<String> getMetaData(String userId) {
        return Optional.of(
            client
                .get()
                .uri("/api/v2/users/{userId}?fields=app_metadata", userId)
                .retrieve()
                .toEntity(UserMetadataResponse.class))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .map(UserMetadataResponse::getAppMetadata)
                .map(UserMetadataResponse.AppMetadata::getStripeSubscription);
    }

    public void removeSubscriptionAppMetadata(String userId) {
        String body = "{\"user_metadata\":{},\"app_metadata\":{ \"stripe_subscription\":\"\"}}";
        LOGGER.info("ENVIANDO JSON: {}.", body);
        ResponseSpec responseSpec = client
                .patch()
                .uri("/api/v2/users/{userId}", userId)
                .body(body)
                .retrieve();

        ResponseEntity<Void> response = responseSpec.toBodilessEntity();
        if (HttpStatus.OK.equals(response.getStatusCode())) {
            LOGGER.info("Atualizacao do usuario Auth0 com a remocao do subscriptionId da Stripe, realizada com sucesso.");
        } else {
            LOGGER.error("Nao foi possivel atualizar o usuario no Auth0 removendo o subscriptionId da Stripe: {}", response.getBody());
        }
    }
}


class UserMetadataResponse {

    @JsonProperty("app_metadata")
    private AppMetadata appMetadata;

    public AppMetadata getAppMetadata() {
        return appMetadata;
    }

    public void setAppMetadata(AppMetadata appMetadata) {
        this.appMetadata = appMetadata;
    }

    public static class AppMetadata {
        @JsonProperty("stripe_subscription")
        private String stripeSubscription;

        public String getStripeSubscription() {
            return stripeSubscription;
        }

        public void setStripeSubscription(String stripeSubscription) {
            this.stripeSubscription = stripeSubscription;
        }
    }
}