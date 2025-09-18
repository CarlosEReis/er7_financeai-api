package com.er7.financeai.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static com.er7.financeai.common.TransactionConstants.*;

import com.er7.financeai.utils.Auth0Utils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerIT {

    @Autowired private Auth0Utils auth0Utils;
    @Autowired private WebTestClient webTestClient;

    @Sql(scripts = { "/remove_transactions.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createTransaction_Successfully() {
        webTestClient
            .post().uri(BASE_URL)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + auth0Utils.obterJwt())
                .bodyValue(TRANSACTION_REQUEST_VALID)
            .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectHeader().value("Location", location -> assertThat(location).contains(BASE_URL + "/1"))
                    .expectBody()
                    .jsonPath("$.id").isNotEmpty()
                    .jsonPath("$.type").isEqualTo(TRANSACTION_REQUEST_VALID.type().toString())
                    .jsonPath("$.name").isEqualTo(TRANSACTION_REQUEST_VALID.name())
                    .jsonPath("$.amount").isEqualTo(TRANSACTION_REQUEST_VALID.amount().intValue())
                    .jsonPath("$.date").isEqualTo(TRANSACTION_REQUEST_VALID.date())
                        .jsonPath("$.category.id").isEqualTo(TRANSACTION_REQUEST_VALID.category().id())
                        .jsonPath("$.category.name").isNotEmpty()
                        .jsonPath("$.paymentMethod.id").isEqualTo(TRANSACTION_REQUEST_VALID.paymentMethod().id())
                        .jsonPath("$.paymentMethod.name").isNotEmpty();
    }

    @Sql(scripts = { "/remove_transactions.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = { "/insert_transactions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getTransactionById_returnTransaction() {
        webTestClient
            .get().uri(BASE_URL + "/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + auth0Utils.obterJwt())
            .exchange()
                .expectStatus().isOk()
                .expectBody()
                    .jsonPath("$.id").isEqualTo(1)
                    .jsonPath("$.name").isNotEmpty()
                    .jsonPath("$.amount").isNotEmpty()
                    .jsonPath("$.type").isNotEmpty()
                    .jsonPath("$.date").isNotEmpty()
                    .jsonPath("$.category.id").isNotEmpty()
                    .jsonPath("$.paymentMethod.id").isNotEmpty();
    }

    @Sql(scripts = { "/remove_transactions.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = { "/insert_transactions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void updateTransaction_returnTransactionUpdated() {
        webTestClient
            .put().uri(BASE_URL + "/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + auth0Utils.obterJwt())
                .bodyValue(TRANSACTION_REQUEST_UPDATE_VALID)
            .exchange()
                .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.type").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.type().toString())
                    .jsonPath("$.name").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.name())
                    .jsonPath("$.amount").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.amount().intValue())
                    .jsonPath("$.date").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.date())
                        .jsonPath("$.category.id").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.category().id())
                        .jsonPath("$.category.name").isNotEmpty()
                        .jsonPath("$.paymentMethod.id").isEqualTo(TRANSACTION_REQUEST_UPDATE_VALID.paymentMethod().id())
                        .jsonPath("$.paymentMethod.name").isNotEmpty();
    }

    @Sql(scripts = { "/remove_transactions.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Sql(scripts = { "/insert_transactions.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void deleteTransaction() {
        webTestClient
            .delete().uri(BASE_URL + "/{id}", 1)
                .header(HttpHeaders.AUTHORIZATION,"Bearer " + auth0Utils.obterJwt())
            .exchange()
                .expectStatus().isNoContent();
    }

}
