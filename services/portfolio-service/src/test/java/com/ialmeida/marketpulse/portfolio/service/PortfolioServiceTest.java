package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void shouldReturnAllPortfolios() {

        Portfolio portfolio = new Portfolio(
            "My ETF Portfolio",
            "EUR",
            Instant.now()
        );

        when(portfolioRepository.findAll())
            .thenReturn(List.of(portfolio));

        List<PortfolioResponse> result =
            portfolioService.getPortfolios();

        assertEquals(1, result.size());
        assertEquals(
            "My ETF Portfolio",
            result.getFirst().getName()
        );
        assertEquals(
            "EUR",
            result.getFirst().getBaseCurrency()
        );
    }

    @Test
    void shouldCreatePortfolio() {

        CreatePortfolioRequest request =
            org.mockito.Mockito.mock(CreatePortfolioRequest.class);

        when(request.getName())
            .thenReturn("My ETF Portfolio");

        when(request.getBaseCurrency())
            .thenReturn("EUR");

        Portfolio portfolio = new Portfolio(
            "My ETF Portfolio",
            "EUR",
            Instant.now()
        );

        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolio);

        PortfolioResponse result =
            portfolioService.createPortfolio(request);

        assertEquals(
            "My ETF Portfolio",
            result.getName()
        );

        assertEquals(
            "EUR",
            result.getBaseCurrency()
        );

        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void shouldReturnPortfolioById() {

        Portfolio portfolio = new Portfolio(
            "My ETF Portfolio",
            "EUR",
            Instant.now()
        );

        when(portfolioRepository.findById(1L))
            .thenReturn(Optional.of(portfolio));

        PortfolioResponse result =
            portfolioService.getPortfolio(1L);

        assertEquals(
            "My ETF Portfolio",
            result.getName()
        );

        assertEquals(
            "EUR",
            result.getBaseCurrency()
        );
    }

    @Test
    void shouldThrowExceptionWhenPortfolioDoesNotExist() {

        when(portfolioRepository.findById(999L))
            .thenReturn(Optional.empty());

        PortfolioNotFoundException exception =
            assertThrows(
                PortfolioNotFoundException.class,
                () -> portfolioService.getPortfolio(999L)
            );

        assertEquals(
            "Portfolio with id 999 was not found.",
            exception.getMessage()
        );
    }

    @Test
    void shouldUpdatePortfolio() {

        UpdatePortfolioRequest request =
            org.mockito.Mockito.mock(UpdatePortfolioRequest.class);

        when(request.getName())
            .thenReturn("Updated Portfolio");

        when(request.getBaseCurrency())
            .thenReturn("USD");

        Portfolio portfolio = new Portfolio(
            "My ETF Portfolio",
            "EUR",
            Instant.now()
        );

        when(portfolioRepository.findById(1L))
            .thenReturn(Optional.of(portfolio));

        when(portfolioRepository.save(any(Portfolio.class)))
            .thenReturn(portfolio);

        PortfolioResponse result =
            portfolioService.updatePortfolio(1L, request);

        assertEquals(
            "Updated Portfolio",
            result.getName()
        );

        assertEquals(
            "USD",
            result.getBaseCurrency()
        );

        verify(portfolioRepository).findById(1L);
        verify(portfolioRepository).save(portfolio);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPortfolio() {

        UpdatePortfolioRequest request =
            org.mockito.Mockito.mock(UpdatePortfolioRequest.class);

        when(portfolioRepository.findById(999L))
            .thenReturn(Optional.empty());

        PortfolioNotFoundException exception =
            assertThrows(
                PortfolioNotFoundException.class,
                () -> portfolioService.updatePortfolio(
                    999L,
                    request
                )
            );

        assertEquals(
            "Portfolio with id 999 was not found.",
            exception.getMessage()
        );
    }

    @Test
    void shouldDeletePortfolio() {

        Portfolio portfolio = new Portfolio(
            "My ETF Portfolio",
            "EUR",
            Instant.now()
        );

        when(portfolioRepository.findById(1L))
            .thenReturn(Optional.of(portfolio));

        portfolioService.deletePortfolio(1L);

        verify(portfolioRepository).findById(1L);
        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPortfolio() {

        when(portfolioRepository.findById(999L))
            .thenReturn(Optional.empty());

        PortfolioNotFoundException exception =
            assertThrows(
                PortfolioNotFoundException.class,
                () -> portfolioService.deletePortfolio(999L)
            );

        assertEquals(
            "Portfolio with id 999 was not found.",
            exception.getMessage()
        );
    }
}