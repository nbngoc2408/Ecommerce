package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.entity.Customers;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private final static String EMAIL_KEY = "EMAIL";
    private final static String VERIFY_KEY = "VERIFY";

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(Customers customers) {
        return JWT.create().withClaim(EMAIL_KEY, customers.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() * (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(Customers customers) {
        return JWT.create().withClaim(VERIFY_KEY, customers.getFirstName())
                .withExpiresAt(new Date(System.currentTimeMillis() * (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUserEmail(String token) {
        return JWT.decode(token).getClaim(EMAIL_KEY).asString();
    }
}
