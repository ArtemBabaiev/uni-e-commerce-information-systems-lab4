package com.ababaiev.atmserver.services;

import com.ababaiev.atmserver.models.BankAccount;
import com.ababaiev.atmserver.models.BankCard;
import com.ababaiev.atmserver.models.requests.AddMoneyRequest;
import com.ababaiev.atmserver.models.requests.WithdrawMoneyRequest;
import com.ababaiev.atmserver.models.responses.CreateCardResponse;
import com.ababaiev.atmserver.repositories.AccountRepo;
import com.ababaiev.atmserver.repositories.CardRepo;
import com.ababaiev.atmserver.utils.AddFundsStatus;
import com.ababaiev.atmserver.utils.CryptoUtils;
import com.ababaiev.atmserver.utils.WithdrawStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.ByteBuffer;

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

        double amount = Math.abs(request.getAmount());

        if (!validateCardDetails(optCard.get(), request.getCvv(), request.getPvv())){
            return WithdrawStatus.REJECTED;
        }

        BankAccount bankAccount = optCard.get().getAccount();

        if (bankAccount.getBalance() < amount) {
            return WithdrawStatus.REJECTED;
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        accountRepo.save(bankAccount);
        return WithdrawStatus.APPROVED;
    }

    public AddFundsStatus addMoney(AddMoneyRequest request) {
        var optCard = this.cardRepo.findById(request.getCardNumber());
        if (optCard.isEmpty()){
            return AddFundsStatus.FAILED;
        }

        double amount = Math.abs(request.getAmount());

        if (!validateCardDetails(optCard.get(), request.getCvv(), request.getPvv())){
            return AddFundsStatus.FAILED;
        }

        BankAccount bankAccount = optCard.get().getAccount();
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        accountRepo.save(bankAccount);
        return AddFundsStatus.SUCCESS;
    }

    private boolean validateCardDetails(BankCard card, String requestCvv, String requestPvv) {
        BankAccount bankAccount = card.getAccount();
        String accountNumber = bankAccount.getAccountNumber();
        String cardNumber = card.getCardNumber();

        String verificationCvv = generateCvv(accountNumber, cardNumber);
        if (!verificationCvv.equals(requestCvv) || !verificationCvv.equals(card.getCvv())) {
            return false;
        }

        if (!card.getPvv().equals(requestPvv)){
            return false;
        }
        return true;
    }

    private String generateCvv(String accountNumber, String cardNumber) {
        String concat = accountNumber + cardNumber;
        byte[] hash = CryptoUtils.tripleHash(concat);
        return hashToNumberAlt(hash).mod(new BigInteger("1000")).toString();
//        long number = hashToNumber(hash);
//        return String.valueOf(number);
    }

    private String generatePvv(String pin) {
        byte[] hash = CryptoUtils.tripleHash(pin);
        return hashToNumberAlt(hash).mod(new BigInteger("10000")).toString();
//        long number = hashToNumber(hash);
//        return String.valueOf(number);
    }

    private BigInteger hashToNumberAlt(byte[] hash) {
        String number = new BigInteger(1, hash).toString();
        StringBuilder sb = new StringBuilder();
        char[] charArray = number.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (i % 2 == 0) {
                sb.append(c);
            }
        }
        return new BigInteger(sb.toString());

    }

    private long hashToNumber(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long result = buffer.getLong() ^ buffer.getLong();
        return Math.abs(result);
    }
}
