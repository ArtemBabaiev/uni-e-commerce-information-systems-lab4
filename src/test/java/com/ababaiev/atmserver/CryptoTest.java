package com.ababaiev.atmserver;

import com.ababaiev.atmserver.services.BankService;
import com.ababaiev.atmserver.utils.CryptoUtils;
import org.junit.jupiter.api.Test;

public class CryptoTest {
    @Test
    public void test() {
        BankService bankService = new BankService(null, null);
        bankService.createBankAccount();
    }
}
