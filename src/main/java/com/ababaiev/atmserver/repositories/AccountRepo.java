package com.ababaiev.atmserver.repositories;

import com.ababaiev.atmserver.models.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<BankAccount, String> {
}
