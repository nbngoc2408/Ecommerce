package com.example.demo.repository;

import com.example.demo.entity.Customers;
import com.example.demo.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByCustomers(Customers customers);

    List<VerificationToken> findByCustomers_EmailIgnoreCaseOrderByIdDesc(String email);
}
