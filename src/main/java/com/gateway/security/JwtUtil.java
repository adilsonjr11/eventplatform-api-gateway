package com.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    // Injeta a chave do application.yml (com um fallback seguro para dev se necessário)
    @Value("${jwt.secret:sua-chave-secreta-padrao-com-mais-de-trinta-e-dois-caracteres}")
    private String secret;

    private SecretKey getSigningKey() {
        // Garante que a chave atende aos requisitos de tamanho do HMAC-SHA
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}