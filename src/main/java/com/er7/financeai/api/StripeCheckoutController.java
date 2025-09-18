package com.er7.financeai.api;

import com.er7.financeai.domain.service.Auth0Service;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping()
public class StripeCheckoutController   {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.price.id}")
    private String stripePriceId;

    @Value("${stripe.checkout.url.success}")
    private String stripeCheckoutUrlSuccess;

    @Value("${stripe.checkout.url.cancel}")
    private String stripeCheckoutUrlCancel;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    private Auth0Service auth0Service;

    public StripeCheckoutController(Auth0Service auth0Service) {
        this.auth0Service = auth0Service;
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, Object>> createCheckoutSession(Authentication authentication) throws Exception {

        // Crie os parametros da sessao
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Define o tipo de pagamento
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(stripePriceId) // Define o price_id do produto da Stripe
                                .setQuantity(1L) // Quantidade do produto
                                .build())
                .setSubscriptionData( // Configuração para assinatura
                        SessionCreateParams.SubscriptionData.builder()
                                .putMetadata("user_id", authentication.getName()) // add userId do Auth0
                                .build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(stripeCheckoutUrlSuccess)
                .setCancelUrl(stripeCheckoutUrlCancel)
                .build();

        // Cria a sessao
        Session session = Session.create(params);

        // Retorna o ID da sessão para o frontend
        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId()); // Acessando o ID da sessão

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel-plan")
    public String cancelPlan(Authentication authentication) throws StripeException {
        String userId = authentication.getName();

        // remove a role no Auth0
        auth0Service.removeRolePaidIn(userId);

        // pega a subscription da Stripe armazenada no app_metadata do usuario no Auth
        String subscriptionId = auth0Service.getMetaData(userId).get();

        // encerra do pagamento do usuário na Stripe
        Subscription subscription = Subscription.retrieve(subscriptionId);
        Subscription canceledSubscription = subscription.cancel();

        // remove a subscription da Stripe armazenada no app_metadata do usuario no Auth
        auth0Service.removeSubscriptionAppMetadata(userId);

        return canceledSubscription.getId();
    }

}
