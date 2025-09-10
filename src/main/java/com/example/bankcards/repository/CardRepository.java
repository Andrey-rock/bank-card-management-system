package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


/**
 * Репозиторий для карт
 *
 * @author Andrei Bronskijj, 2025
 * @version 0.0.1
 */
@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    Page<Card> findByOwnerId(Long owner_id, Pageable pageable);
}
