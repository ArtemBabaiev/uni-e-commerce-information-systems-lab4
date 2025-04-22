package com.ababaiev.atmserver.controllers;

import com.ababaiev.atmserver.models.requests.AddMoneyRequest;
import com.ababaiev.atmserver.models.requests.WithdrawMoneyRequest;
import com.ababaiev.atmserver.models.responses.CreateCardResponse;
import com.ababaiev.atmserver.services.BankService;
import com.ababaiev.atmserver.utils.AddFundsStatus;
import com.ababaiev.atmserver.utils.WithdrawStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankController {

    private BankService bankService;
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/create-bank-card")
    public CreateCardResponse createBankCard() {
        return this.bankService.createBankAccount();
    }

    @PostMapping("/withdraw-money")
    public String withdrawMoney(@RequestBody WithdrawMoneyRequest request) {
        return this.bankService.withdrawMoney(request).toString();
    }

    @PostMapping("/add-money")
    public String addMoney(@RequestBody AddMoneyRequest request) {
        return this.bankService.addMoney(request).toString();
    }
}
