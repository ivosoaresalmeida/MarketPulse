package com.ialmeida.marketpulse.portfolio.controller;

import com.ialmeida.marketpulse.portfolio.dto.CreatePositionRequest;
import com.ialmeida.marketpulse.portfolio.dto.PositionResponse;
import com.ialmeida.marketpulse.portfolio.dto.UpdatePositionRequest;
import com.ialmeida.marketpulse.portfolio.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portfolios/{portfolioId}/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PositionResponse> createPosition(
            @PathVariable Long portfolioId,
            @Valid @RequestBody CreatePositionRequest request
    ) {

        PositionResponse response =
                positionService.createPosition(portfolioId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PositionResponse>> getPositions(
            @PathVariable Long portfolioId
    ) {

        return ResponseEntity.ok(
                positionService.getPositions(portfolioId)
        );
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<PositionResponse> getPosition(
            @PathVariable Long portfolioId,
            @PathVariable Long positionId
    ) {

        return ResponseEntity.ok(
                positionService.getPosition(
                        portfolioId,
                        positionId
                )
        );
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<PositionResponse> updatePosition(
            @PathVariable Long portfolioId,
            @PathVariable Long positionId,
            @Valid @RequestBody UpdatePositionRequest request
    ) {

        return ResponseEntity.ok(
                positionService.updatePosition(
                        portfolioId,
                        positionId,
                        request
                )
        );
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Void> deletePosition(
            @PathVariable Long portfolioId,
            @PathVariable Long positionId
    ) {

        positionService.deletePosition(
                portfolioId,
                positionId
        );

        return ResponseEntity.noContent().build();
    }
}