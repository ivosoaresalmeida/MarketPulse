package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.client.UserClient;
import com.ialmeida.marketpulse.portfolio.dto.CreatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.dto.PortfolioResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePortfolioRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.exception.UserNotFoundException;
import com.ialmeida.marketpulse.portfolio.mapper.PortfolioMapper;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioMapper portfolioMapper;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setupMapper() {
        lenient().when(portfolioMapper.toResponse(any(Portfolio.class)))
            .thenAnswer(invocation -> {
                Portfolio portfolio = invocation.getArgument(0);
                return new PortfolioResponse(
                    portfolio.getId(),
                    portfolio.getUserId(),
                    portfolio.getName(),
                    portfolio.getBaseCurrency(),
                    List.of(),
                    portfolio.getCreatedAt(),
                    portfolio.getUpdatedAt()
                );
            });
    }

    @Test
    void shouldReturnAllPortfolios() {

        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "EUR"
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

        when(request.getUserId())
            .thenReturn(1L);

        when(request.getBaseCurrency())
            .thenReturn("EUR");

        when(userClient.existsById(1L)).thenReturn(true);

        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "EUR"
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
        assertEquals(1L, result.getUserId());

        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void shouldThrowWhenCreatingPortfolioWithNonExistingUser() {

        CreatePortfolioRequest request =
            org.mockito.Mockito.mock(CreatePortfolioRequest.class);

        when(request.getUserId()).thenReturn(999L);
        when(userClient.existsById(999L)).thenReturn(false);

        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> portfolioService.createPortfolio(request)
        );

        assertEquals(
            "User with id 999 was not found.",
            exception.getMessage()
        );
    }

    @Test
    void shouldReturnPortfolioById() {

        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "EUR"
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
        assertEquals(1L, result.getUserId());
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
            1L,
            "My ETF Portfolio",
            "EUR"
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
        assertEquals(1L, result.getUserId());

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
            1L,
            "My ETF Portfolio",
            "EUR"
        );

        when(portfolioRepository.findById(1L))
            .thenReturn(Optional.of(portfolio));

        portfolioService.deletePortfolio(1L);

        verify(portfolioRepository).findById(1L);
        verify(portfolioRepository).delete(portfolio);
    }

    @Test
    void shouldReturnPortfoliosByUserId() {
        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "EUR"
        );

        when(portfolioRepository.findByUserId(1L))
            .thenReturn(List.of(portfolio));

        List<PortfolioResponse> result =
            portfolioService.getPortfoliosByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getUserId());
        assertEquals("My ETF Portfolio", result.getFirst().getName());
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