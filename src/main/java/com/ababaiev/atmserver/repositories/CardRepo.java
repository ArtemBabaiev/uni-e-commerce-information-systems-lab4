package com.ababaiev.atmserver.repositories;

import com.ababaiev.atmserver.models.BankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepo extends JpaRepository<BankCard, String> {
}
