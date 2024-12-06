package com.example.demo.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.demo.entity.Customers;
import com.example.demo.repository.CustomersRepository;
import com.example.demo.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {
    private JWTService jwtService;

    private CustomersRepository customersRepository;

    public JWTRequestFilter(JWTService jwtService, CustomersRepository customersRepository) {
        this.jwtService = jwtService;
        this.customersRepository = customersRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                String userEmail = jwtService.getUserEmail(token);
                Optional<Customers> customers = customersRepository.findByEmail(userEmail);
                if (customers.isPresent()) {
                    Customers user = customers.get();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (JWTDecodeException ignored){
            }
        }

        //notify to system that this filter is success and go to next filter.
        filterChain.doFilter(request, response);
    }
}
