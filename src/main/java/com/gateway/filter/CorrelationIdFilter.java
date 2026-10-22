package com.gateway.filter;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered; // Import necessário se quiser definir a ordem
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String correlationId =
                UUID.randomUUID().toString();

        var mutatedRequest = exchange.getRequest()
                .mutate()
                .header("X-Correlation-Id", correlationId)
                .build();

        return chain.filter(
                exchange.mutate().request(mutatedRequest).build()
        );
    }

    @Override
    public int getOrder() {
        return -1;
    }
}