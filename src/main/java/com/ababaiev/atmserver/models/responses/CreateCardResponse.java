package com.ababaiev.atmserver.models.responses;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCardResponse {
    private String cardNumber;
    private String pin;
    private String cvv;
    private double balance;
}
