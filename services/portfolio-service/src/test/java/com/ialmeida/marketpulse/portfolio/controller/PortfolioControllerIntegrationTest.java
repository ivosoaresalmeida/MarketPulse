package com.ialmeida.marketpulse.portfolio.controller;

import com.ialmeida.marketpulse.portfolio.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

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

    @MockitoBean
    private UserClient userClient;

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
        when(userClient.existsById(1L)).thenReturn(true);

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 1,
                                    "name": "Integration Test Portfolio",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId")
                .value(1))
            .andExpect(jsonPath("$.name")
                .value("Integration Test Portfolio"))
            .andExpect(jsonPath("$.baseCurrency")
                .value("EUR"));
    }

    @Test
    void shouldReturnPortfolios() throws Exception {
        when(userClient.existsById(1L)).thenReturn(true);

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 1,
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
            .andExpect(jsonPath("$[*].userId", hasItem(1)))
            .andExpect(jsonPath("$[*].name", hasItem("Integration Test Portfolio")))
            .andExpect(jsonPath("$[*].baseCurrency", hasItem("EUR")));
    }

    @Test
    void shouldReturnPortfolioById() throws Exception {
        when(userClient.existsById(1L)).thenReturn(true);

        String response = mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 1,
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
            .andExpect(jsonPath("$.userId")
                .value(1))
            .andExpect(jsonPath("$.name")
                .value("Portfolio By ID Test"))
            .andExpect(jsonPath("$.baseCurrency")
                .value("EUR"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingPortfolioWithMissingUser() throws Exception {
        when(userClient.existsById(999L)).thenReturn(false);

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 999,
                                    "name": "Integration Test Portfolio",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPortfoliosByUserId() throws Exception {
        when(userClient.existsById(1L)).thenReturn(true);
        when(userClient.existsById(2L)).thenReturn(true);

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 1,
                                    "name": "User One Portfolio",
                                    "baseCurrency": "EUR"
                                }
                                """)
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                post("/portfolios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                                {
                                    "userId": 2,
                                    "name": "User Two Portfolio",
                                    "baseCurrency": "USD"
                                }
                                """)
            )
            .andExpect(status().isCreated());

        mockMvc.perform(
                get("/portfolios")
                    .queryParam("userId", "1")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[*].userId", hasItem(1)))
            .andExpect(jsonPath("$[*].name", hasItem("User One Portfolio")))
            .andExpect(jsonPath("$[*].name", not(hasItem("User Two Portfolio"))));
    }
}