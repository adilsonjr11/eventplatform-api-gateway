package com.gateway.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrelationIdFilterTest {

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private CorrelationIdFilter correlationIdFilter;

    @Test
    void deveAdicionarCorrelationIdNaRequisicao() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/events").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Configura o mock do filterChain para interceptar o exchange modificado e validar o header
        when(filterChain.filter(any())).thenAnswer(invocation -> {
            org.springframework.web.server.ServerWebExchange modifiedExchange = invocation.getArgument(0);
            String correlationHeader = modifiedExchange.getRequest().getHeaders().getFirst("X-Correlation-Id");

            // Garante que o Correlation-Id foi gerado e inserido com sucesso
            assertNotNull(correlationHeader);

            return Mono.empty();
        });

        // Act
        correlationIdFilter.filter(exchange, filterChain).block();

        // Assert
        verify(filterChain, times(1)).filter(any());
    }
}