package com.ialmeida.marketpulse.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreatePortfolioRequest {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Portfolio name is required.")
    @Size(
        max = 100,
        message = "Portfolio name must not exceed 100 characters."
    )
    private String name;

    @NotBlank(message = "Base currency is required.")
    @Pattern(
        regexp = "^[A-Z]{3}$",
        message = "Base currency must be a 3-letter uppercase currency code."
    )
    private String baseCurrency;

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
}