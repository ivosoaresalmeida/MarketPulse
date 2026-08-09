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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class PortfolioControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("marketpulse")
            .withUsername("postgres")
            .withPassword("postgres");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configureProperties(
        DynamicPropertyRegistry registry) {

        registry.add(
            "spring.datasource.url",
            postgres::getJdbcUrl
        );

        registry.add(
            "spring.datasource.username",
            postgres::getUsername
        );

        registry.add(
            "spring.datasource.password",
            postgres::getPassword
        );
    }

    @Test
    void shouldCreatePortfolio() throws Exception {

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "name": "Integration Test Portfolio",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name")
                .value("Integration Test Portfolio"))
            .andExpect(jsonPath("$.baseCurrency")
                .value("EUR"));
    }

    @Test
    void shouldReturnPortfolios() throws Exception {

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "name": "Integration Test Portfolio",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                get("/portfolios")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name")
                .value("Integration Test Portfolio"))
            .andExpect(jsonPath("$[0].baseCurrency")
                .value("EUR"));
    }

    @Test
    void shouldReturnPortfolioById() throws Exception {

        String response = mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "name": "Portfolio By ID Test",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        String id = com.jayway.jsonpath.JsonPath
            .read(response, "$.id")
            .toString();

        mockMvc.perform(
                get("/portfolios/{id}", id)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id")
                .value(Integer.parseInt(id)))
            .andExpect(jsonPath("$.name")
                .value("Portfolio By ID Test"))
            .andExpect(jsonPath("$.baseCurrency")
                .value("EUR"));
    }
}