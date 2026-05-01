package com.demo.api_gestion_visitas.infrastructure.security;

import com.demo.api_gestion_visitas.domain.model.Profile;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Objects;

@Component
public class JwtTokenProvider {
    private final JwtProperties properties;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    public String generateToken(String usuario, Profile perfil) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(usuario)
                .claim("perfil", perfil.name())
                .issuedAt(new java.util.Date(now))
                .expiration(new java.util.Date(now + properties.expirationMs()))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void setAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
        String usuario = claims.getSubject();
        String perfil = Objects.toString(claims.get("perfil"), "");
        var authentication = new UsernamePasswordAuthenticationToken(
                usuario,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + perfil))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
