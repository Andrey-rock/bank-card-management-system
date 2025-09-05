package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.net.ContentHandler;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByCardNumber(long number);

    @Modifying
    @Transactional
    void deleteByCardNumber(long number);

    boolean existsByCardNumber(long cardNumber);

    Page<Card> findByOwnerId(Long owner_id, Pageable pageable);

    Page<Card> findByOwnerIdAndCardNumber(Long owner_id, Long cardNumber, Pageable pageable);
}
