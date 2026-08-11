package com.ialmeida.marketpulse.portfolio.service;

import com.ialmeida.marketpulse.portfolio.dto.CreatePositionRequest;
import com.ialmeida.marketpulse.portfolio.dto.PositionResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePositionRequest;
import com.ialmeida.marketpulse.portfolio.exception.PortfolioNotFoundException;
import com.ialmeida.marketpulse.portfolio.exception.PositionNotFoundException;
import com.ialmeida.marketpulse.portfolio.model.Position;
import com.ialmeida.marketpulse.portfolio.model.Portfolio;
import com.ialmeida.marketpulse.portfolio.repository.PositionRepository;
import com.ialmeida.marketpulse.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PositionService positionService;

    @Test
    void shouldCreatePosition() {
        CreatePositionRequest request = org.mockito.Mockito.mock(CreatePositionRequest.class);

        when(request.getSymbol()).thenReturn("AAPL");
        when(request.getQuantity()).thenReturn(BigDecimal.valueOf(10));
        when(request.getAverageCost()).thenReturn(BigDecimal.valueOf(180.50));
        when(request.getCurrency()).thenReturn("USD");

        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "USD"
        );

        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolio));

        Position position = new Position(
            portfolio,
            "AAPL",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(180.50),
            "USD"
        );

        when(positionRepository.save(any(Position.class))).thenReturn(position);

        PositionResponse response = positionService.createPosition(1L, request);

        assertEquals("AAPL", response.getSymbol());
        assertEquals("10", response.getQuantity());
        assertEquals("180.5", response.getAverageCost());
        assertEquals("USD", response.getCurrency());

        verify(portfolioRepository).findById(1L);
        verify(positionRepository).save(any(Position.class));
    }

    @Test
    void shouldReturnPositionsForPortfolio() {
        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "USD"
        );

        when(portfolioRepository.existsById(1L)).thenReturn(true);

        Position position = new Position(
            portfolio,
            "GOOGL",
            BigDecimal.valueOf(5),
            BigDecimal.valueOf(120.75),
            "USD"
        );

        when(positionRepository.findByPortfolioId(1L))
            .thenReturn(List.of(position));

        List<PositionResponse> result = positionService.getPositions(1L);

        assertEquals(1, result.size());
        assertEquals("GOOGL", result.get(0).getSymbol());
        assertEquals("5", result.get(0).getQuantity());
        assertEquals("120.75", result.get(0).getAverageCost());
    }

    @Test
    void shouldReturnPositionById() {
        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "USD"
        );

        when(portfolioRepository.existsById(1L)).thenReturn(true);

        Position position = new Position(
            portfolio,
            "MSFT",
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(310.10),
            "USD"
        );

        when(positionRepository.findByIdAndPortfolioId(2L, 1L))
            .thenReturn(Optional.of(position));

        PositionResponse response = positionService.getPosition(1L, 2L);

        assertEquals("MSFT", response.getSymbol());
        assertEquals("3", response.getQuantity());
        assertEquals("310.1", response.getAverageCost());
    }

    @Test
    void shouldUpdatePosition() {
        UpdatePositionRequest request = org.mockito.Mockito.mock(UpdatePositionRequest.class);

        when(request.getSymbol()).thenReturn("NFLX");
        when(request.getQuantity()).thenReturn(BigDecimal.valueOf(2));
        when(request.getAverageCost()).thenReturn(BigDecimal.valueOf(420.25));
        when(request.getCurrency()).thenReturn("USD");

        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "USD"
        );

        Position position = new Position(
            portfolio,
            "TSLA",
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(700),
            "USD"
        );

        when(portfolioRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.findByIdAndPortfolioId(2L, 1L))
            .thenReturn(Optional.of(position));
        when(positionRepository.save(position)).thenReturn(position);

        PositionResponse response = positionService.updatePosition(1L, 2L, request);

        assertEquals("NFLX", response.getSymbol());
        assertEquals("2", response.getQuantity());
        assertEquals("420.25", response.getAverageCost());
        assertEquals("USD", response.getCurrency());

        verify(positionRepository).findByIdAndPortfolioId(2L, 1L);
        verify(positionRepository).save(position);
    }

    @Test
    void shouldDeletePosition() {
        Portfolio portfolio = new Portfolio(
            1L,
            "My ETF Portfolio",
            "USD"
        );

        Position position = new Position(
            portfolio,
            "AMZN",
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(150.00),
            "USD"
        );

        when(portfolioRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.findByIdAndPortfolioId(2L, 1L))
            .thenReturn(Optional.of(position));

        positionService.deletePosition(1L, 2L);

        verify(positionRepository).findByIdAndPortfolioId(2L, 1L);
        verify(positionRepository).delete(position);
    }

    @Test
    void shouldThrowWhenPortfolioDoesNotExist() {
        when(portfolioRepository.existsById(999L)).thenReturn(false);

        PortfolioNotFoundException exception = assertThrows(
            PortfolioNotFoundException.class,
            () -> positionService.getPositions(999L)
        );

        assertEquals(
            "Portfolio with id 999 was not found.",
            exception.getMessage()
        );
    }

    @Test
    void shouldThrowWhenPositionDoesNotExist() {
        when(portfolioRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.findByIdAndPortfolioId(999L, 1L))
            .thenReturn(Optional.empty());

        PositionNotFoundException exception = assertThrows(
            PositionNotFoundException.class,
            () -> positionService.getPosition(1L, 999L)
        );

        assertEquals(
            "Position with id 999 was not found.",
            exception.getMessage()
        );
    }
}
