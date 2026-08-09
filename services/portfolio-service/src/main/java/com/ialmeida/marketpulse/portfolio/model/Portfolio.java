package com.ialmeida.marketpulse.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "base_currency", nullable = false, length = 3)
    private String baseCurrency;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Portfolio() {
    }

    public Portfolio(
        String name,
        String baseCurrency,
        Instant createdAt) {

        this.name = name;
        this.baseCurrency = baseCurrency;
        this.createdAt = createdAt;
    }

    public void update(String name, String baseCurrency) {
        this.name = name;
        this.baseCurrency = baseCurrency;
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