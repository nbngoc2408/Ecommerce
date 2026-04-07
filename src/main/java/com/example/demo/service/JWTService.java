package com.example.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.Customer;
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
    private final static String RESET_PASSWORD_EMAIL_VERIFY = "RESET_PASSWORD_VERIFY";

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    public String generateJWT(Customer customers) {
        return JWT.create().withClaim(EMAIL_KEY, customers.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 30)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateVerificationJWT(Customer customers) {
        return JWT.create().withClaim(VERIFY_KEY, customers.getFirstName())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String generateResetPassword(Customer customers) {
        return JWT.create().withClaim(RESET_PASSWORD_EMAIL_VERIFY, customers.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000L * 60 * 5)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    public String getUserEmail(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token); //verify token (expired or using other algorithms)
        return jwt.getClaim(EMAIL_KEY).asString();
    }

    public String getEmailResetPassword(String token) {
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token); //verify token (expired or using other algorithms)
        return jwt.getClaim(RESET_PASSWORD_EMAIL_VERIFY).asString();
    }
}
