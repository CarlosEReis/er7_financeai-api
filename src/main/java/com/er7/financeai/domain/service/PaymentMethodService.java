package com.er7.financeai.domain.service;

import com.er7.financeai.domain.model.PaymentMethod;
import com.er7.financeai.domain.repository.PaymentMethodRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    public PaymentMethod findById(Long id) {
        return paymentMethodRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment method not found"));
    }
}
