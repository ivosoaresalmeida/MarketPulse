package com.ialmeida.marketpulse.portfolio.controller;

import java.util.List;

import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolios")
    public List<PortfolioResponse> getPortfolios() {
        return portfolioService.getPortfolios();
    }

    @PostMapping("/portfolios")
    public PortfolioResponse createPortfolio(
        @Valid @RequestBody CreatePortfolioRequest request) {

        return portfolioService.createPortfolio(request);
    }
}