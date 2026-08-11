package com.ialmeida.marketpulse.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @NotBlank(message = "Username is required.")
    @Size(
        max = 100,
        message = "Username must not exceed 100 characters."
    )
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    @Size(
        max = 255,
        message = "Email must not exceed 255 characters."
    )
    private String email;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}