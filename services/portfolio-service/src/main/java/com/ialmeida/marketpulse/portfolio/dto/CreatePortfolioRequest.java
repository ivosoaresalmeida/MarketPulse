package com.ialmeida.marketpulse.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePortfolioRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(min = 3, max = 3)
    private String baseCurrency;

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
}