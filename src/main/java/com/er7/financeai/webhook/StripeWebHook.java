package com.er7.financeai.webhook;

import com.er7.financeai.domain.service.Auth0Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/webhook")
public class StripeWebHook {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    private static final Logger LOGGER = LoggerFactory.getLogger(StripeWebHook.class);
    private Auth0Service auth0Service;

    public StripeWebHook(Auth0Service auth0Service) {
        this.auth0Service = auth0Service;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verifique a assinatura do webhook para garantir que o evento seja legítimo
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            // TODO: tratar os demais eventos
            switch (event.getType()) {
                case "invoice.paid":
                    LOGGER.info("Evento {} detectado: ", event.getType());
                    String userId = returnUserId(event);
                    Invoice invoice = (Invoice) event.getData().getObject();
                    LOGGER.info("Usuario: {} - Subscription: {}", userId, invoice.getSubscription());
                    this.auth0Service.addRolePaidIn(userId);
                    this.auth0Service.addMetaData(userId, invoice.getSubscription());

                    break;
                default:
                    LOGGER.warn("Evento desconhecido NAO TRATADO {}: ", event.getType());
                    break;
            }

            // Retona uma resposta 200 para confirmar que o webhook foi processado com sucesso
            return ResponseEntity.ok("Evento recebido com sucesso");

        } catch (SignatureVerificationException e) {
            // Se a assinatura for inválida, o evento é ignorado
            LOGGER.error("Assinatura do webhook inválida: {}", e.getMessage());
            return ResponseEntity.status(400).body("Assinatura inválida");
        } catch (Exception e) {
            // Em caso de qualquer outro erro, retorne uma resposta de erro
            LOGGER.error("Erro ao processar webhook: {}", e.getMessage());
            return ResponseEntity.status(500).body("Erro ao processar webhook");
        }
    }

    private String returnUserId(Event event) {
        String userId = null;

        if (!(event.getData().getObject() instanceof Invoice invoice))
            throw new RuntimeException("Objeto do evento nao e um invoice");

        if (invoice.getSubscriptionDetails() != null && invoice.getSubscriptionDetails().getMetadata() != null)
            userId = invoice.getSubscriptionDetails().getMetadata().get("user_id");

        if (userId == null)
            throw new RuntimeException("user_id não encontrado no metadata.");

        return userId;
    }
}
