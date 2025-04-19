package com.ababaiev.atmserver.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawMoneyRequest {
    @JsonProperty("CardNumber")
    private String cardNumber;

    @JsonProperty("PVV")
    private String pvv;

    @JsonProperty("CVV")
    private String cvv;

    @JsonProperty("Amount")
    private double amount;
}
