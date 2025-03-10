package com.er7.financeai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((auhorize) -> auhorize
                        .requestMatchers("/cancel-plan","/v1/report-ai","/v1/report-ai/resume", "/v1/report-ai/**","/v1/transactions", "/v1/transactions/**","/v1/transactions/statistics/total-per-category", "/v1/transactions/statistics/balance","/create-checkout-session").authenticated()
                        .requestMatchers("/webhook").permitAll()
                )
                .cors(t -> t.configure(http))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/webhook")  // Desabilita CSRF para o webhook
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();
    }

}
