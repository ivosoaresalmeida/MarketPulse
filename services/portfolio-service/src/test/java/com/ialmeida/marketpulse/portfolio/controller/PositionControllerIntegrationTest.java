package com.ialmeida.marketpulse.portfolio.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PositionControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("marketpulse")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void shouldCreateAndRetrievePosition() throws Exception {
        String portfolioResponse = mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "Integration Portfolio",
                            "baseCurrency": "USD"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number portfolioIdValue = com.jayway.jsonpath.JsonPath
            .read(portfolioResponse, "$.id");
        Long portfolioId = portfolioIdValue.longValue();

        String positionResponse = mockMvc.perform(
                post("/portfolios/{portfolioId}/positions", portfolioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "symbol": "AAPL",
                            "quantity": 5,
                            "averageCost": 150.50,
                            "currency": "USD"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol").value("AAPL"))
            .andExpect(jsonPath("$.quantity").value("5"))
            .andExpect(jsonPath("$.averageCost").value("150.50"))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number positionIdValue = com.jayway.jsonpath.JsonPath
            .read(positionResponse, "$.id");
        Long positionId = positionIdValue.longValue();

        mockMvc.perform(
                get("/portfolios/{portfolioId}/positions", portfolioId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].symbol").value("AAPL"))
            .andExpect(jsonPath("$[0].quantity").value("5.00000000"));

        mockMvc.perform(
                get("/portfolios/{portfolioId}/positions/{positionId}", portfolioId, positionId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.symbol").value("AAPL"));
    }

    @Test
    void shouldUpdateAndDeletePosition() throws Exception {
        String portfolioResponse = mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "name": "Integration Portfolio",
                            "baseCurrency": "USD"
                        }
                        """)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number portfolioIdValue = com.jayway.jsonpath.JsonPath
            .read(portfolioResponse, "$.id");
        Long portfolioId = portfolioIdValue.longValue();

        String positionResponse = mockMvc.perform(
                post("/portfolios/{portfolioId}/positions", portfolioId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "symbol": "NFLX",
                            "quantity": 2,
                            "averageCost": 420.25,
                            "currency": "USD"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Number positionIdValue = com.jayway.jsonpath.JsonPath
            .read(positionResponse, "$.id");
        Long positionId = positionIdValue.longValue();

        mockMvc.perform(
                put("/portfolios/{portfolioId}/positions/{positionId}", portfolioId, positionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "symbol": "NFLX",
                            "quantity": 3,
                            "averageCost": 425.00,
                            "currency": "USD"
                        }
                        """)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.quantity").value("3"))
            .andExpect(jsonPath("$.averageCost").value("425.00"));

        mockMvc.perform(
                delete("/portfolios/{portfolioId}/positions/{positionId}", portfolioId, positionId)
            )
            .andExpect(status().isNoContent());
    }
}