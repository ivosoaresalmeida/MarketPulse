package com.ialmeida.marketpulse.portfolio.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(
        @Value("${user-service.url}") String userServiceUrl
    ) {
        this.restClient = RestClient.builder()
            .baseUrl(userServiceUrl)
            .build();
    }

    public boolean existsById(Long userId) {
        try {
            restClient.get()
                .uri("/users/{id}", userId)
                .retrieve()
                .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound exception) {
            return false;
        }
    }
}
