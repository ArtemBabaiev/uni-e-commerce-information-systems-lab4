package com.ababaiev.atmserver.services;

import com.ababaiev.atmserver.models.BankAccount;
import com.ababaiev.atmserver.models.BankCard;
import com.ababaiev.atmserver.models.requests.WithdrawMoneyRequest;
import com.ababaiev.atmserver.models.responses.CreateCardResponse;
import com.ababaiev.atmserver.repositories.AccountRepo;
import com.ababaiev.atmserver.repositories.CardRepo;
import com.ababaiev.atmserver.utils.CryptoUtils;
import com.ababaiev.atmserver.utils.WithdrawStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;

@Service
public class BankService {
    private AccountRepo accountRepo;
    private CardRepo cardRepo;

    public BankService(AccountRepo accountRepo, CardRepo cardRepo) {
        this.accountRepo = accountRepo;
        this.cardRepo = cardRepo;
    }

    @Transactional
    public CreateCardResponse createBankAccount() {
        String accountNumber = CryptoUtils.generateRandomDigits(14);
        String cardNumber = CryptoUtils.generateRandomDigits(16);
        String pin = CryptoUtils.generateRandomDigits(4);
        String cvv = generateCvv(accountNumber, cardNumber);
        String pvv = generatePvv(pin);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setBalance(0);

        bankAccount= accountRepo.save(bankAccount);

        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(cardNumber);
        bankCard.setCvv(cvv);
        bankCard.setPvv(pvv);
        bankCard.setAccount(bankAccount);

        cardRepo.save(bankCard);

        return CreateCardResponse.builder()
                .cardNumber(bankCard.getCardNumber())
                .balance(bankAccount.getBalance())
                .cvv(bankCard.getCvv())
                .pin(pin)
                .build();

    }


    @Transactional
    public WithdrawStatus withdrawMoney(WithdrawMoneyRequest request) {
        var optCard = this.cardRepo.findById(request.getCardNumber());
        if (optCard.isEmpty()){
            return WithdrawStatus.REJECTED;
        }

        double amount = Math.ceil(request.getAmount());

        BankCard card = optCard.get();
        BankAccount bankAccount = card.getAccount();
        String accountNumber = bankAccount.getAccountNumber();
        String cardNumber = card.getCardNumber();

        String verificationCvv = generateCvv(accountNumber, cardNumber);
        if (!verificationCvv.equals(request.getCvv()) || !verificationCvv.equals(card.getCvv())) {
            return WithdrawStatus.REJECTED;
        }

        if (!card.getPvv().equals(request.getPvv())){
            return WithdrawStatus.REJECTED;
        }

        if (bankAccount.getBalance() < amount) {
            return WithdrawStatus.REJECTED;
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        accountRepo.save(bankAccount);
        return WithdrawStatus.APPROVED;
    }

    private String generateCvv(String accountNumber, String cardNumber) {
        String concat = accountNumber + cardNumber;
        byte[] hash = CryptoUtils.tripleHash(concat);
        long number = hashToNumber(hash);
        return String.valueOf(number);
    }

    private String generatePvv(String pin) {
        byte[] hash = CryptoUtils.tripleHash(pin);
        long number = hashToNumber(hash);
        return String.valueOf(number);
    }

    private long hashToNumber(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long result = buffer.getLong() ^ buffer.getLong();
        return Math.abs(result);
    }
}
