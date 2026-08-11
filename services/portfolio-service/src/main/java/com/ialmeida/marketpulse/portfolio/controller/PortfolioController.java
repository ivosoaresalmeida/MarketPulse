package com.ialmeida.marketpulse.portfolio.controller;

import java.util.List;

import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolios")
    public List<PortfolioResponse> getPortfolios(
        @RequestParam(required = false) Long userId
    ) {
        if (userId != null) {
            return portfolioService.getPortfoliosByUserId(userId);
        }

        return portfolioService.getPortfolios();
    }

    @GetMapping("/portfolios/{id}")
    public PortfolioResponse getPortfolio(@PathVariable Long id) {
        return portfolioService.getPortfolio(id);
    }

    @PostMapping("/portfolios")
    @ResponseStatus(HttpStatus.CREATED)
    public PortfolioResponse createPortfolio(
        @Valid @RequestBody CreatePortfolioRequest request) {
        return portfolioService.createPortfolio(request);
    }

    @PutMapping("/portfolios/{id}")
    public PortfolioResponse updatePortfolio(
        @PathVariable Long id,
        @Valid @RequestBody UpdatePortfolioRequest request) {

        return portfolioService.updatePortfolio(id, request);
    }

    @DeleteMapping("/portfolios/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {

        portfolioService.deletePortfolio(id);

        return ResponseEntity.noContent().build();
    }
}