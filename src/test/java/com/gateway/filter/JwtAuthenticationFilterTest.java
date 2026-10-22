package com.gateway.filter;

import com.gateway.exception.UnauthorizedException;
import com.gateway.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void deveLancarExcecaoQuandoCabecalhoAuthorizationEstiverAusente() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/events").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        org.junit.jupiter.api.Assertions.assertThrows(
                UnauthorizedException.class,
                () -> jwtAuthenticationFilter.filter(exchange, filterChain).block()
        );
    }

    @Test
    void devePermitirRequisicaoQuandoTokenForValido() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/events")
                .header("Authorization", "Bearer token_valido_123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Configura os mocks estritamente utilizados neste teste
        when(filterChain.filter(any())).thenReturn(Mono.empty());
        when(jwtUtil.validateToken(anyString())).thenReturn(null);

        // Executa o filtro forçando o bloqueio do Mono para validar o sucesso
        jwtAuthenticationFilter.filter(exchange, filterChain).block();

        verify(jwtUtil, times(1)).validateToken("token_valido_123");
        verify(filterChain, times(1)).filter(any());
    }
}