package com.ialmeida.marketpulse.user.service;

import com.ialmeida.marketpulse.user.dto.CreateUserRequest;
import com.ialmeida.marketpulse.user.exception.DuplicateUserException;
import com.ialmeida.marketpulse.user.model.User;
import com.ialmeida.marketpulse.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void createUserStoresHashedPasswordAndNormalizedEmail() {
        CreateUserRequest request = org.mockito.Mockito.mock(CreateUserRequest.class);
        when(request.getUsername()).thenReturn("Ada");
        when(request.getEmail()).thenReturn("  ADA@EXAMPLE.COM ");
        when(request.getPassword()).thenReturn("plain-secret");
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getEmail()).isEqualTo("ada@example.com");
        assertThat(savedUser.getPassword()).isNotEqualTo("plain-secret");
        assertThat(passwordEncoder.matches("plain-secret", savedUser.getPassword())).isTrue();
    }

    @Test
    void createUserRejectsExistingNormalizedEmail() {
        CreateUserRequest request = org.mockito.Mockito.mock(CreateUserRequest.class);
        when(request.getEmail()).thenReturn(" ADA@EXAMPLE.COM ");
        when(userRepository.existsByEmail("ada@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(DuplicateUserException.class);
    }
}