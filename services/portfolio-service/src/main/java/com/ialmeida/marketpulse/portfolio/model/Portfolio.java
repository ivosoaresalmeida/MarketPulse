package com.ialmeida.marketpulse.portfolio.model;

import java.time.Instant;

public class Portfolio {

    private Long id;
    private String name;
    private String baseCurrency;
    private Instant createdAt;

    public Portfolio(Long id, String name, String baseCurrency, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}