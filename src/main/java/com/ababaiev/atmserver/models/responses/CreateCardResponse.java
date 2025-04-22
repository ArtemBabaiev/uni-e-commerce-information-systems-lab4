package com.ababaiev.atmserver.models.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCardResponse {
    @JsonProperty("CardNumber")
    private String cardNumber;
    @JsonProperty("Pin")
    private String pin;
    @JsonProperty("CVV")
    private String cvv;
    @JsonProperty("Balance")
    private double balance;
}
