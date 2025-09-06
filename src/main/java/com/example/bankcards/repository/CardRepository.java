package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByCardNumber(String number);

    Page<Card> findByOwnerId(Long owner_id, Pageable pageable);

    Page<Card> findByOwnerIdAndCardNumber(Long owner_id, String cardNumber, Pageable pageable);
}
