package com.ialmeida.marketpulse.user.service;

import com.ialmeida.marketpulse.user.dto.CreateUserRequest;
import com.ialmeida.marketpulse.user.dto.UpdateUserRequest;
import com.ialmeida.marketpulse.user.dto.UserResponse;
import com.ialmeida.marketpulse.user.exception.DuplicateUserException;
import com.ialmeida.marketpulse.user.exception.UserNotFoundException;
import com.ialmeida.marketpulse.user.model.User;
import com.ialmeida.marketpulse.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException(email);
        }

        User user = new User(
            request.getUsername(),
            email,
            passwordEncoder.encode(request.getPassword())
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {

        return userRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new UserNotFoundException(id)
            );

        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(
        Long id,
        UpdateUserRequest request
    ) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new UserNotFoundException(id)
            );

        String email = normalizeEmail(request.getEmail());
        if (!user.getEmail().equals(email)
            && userRepository.existsByEmail(email)) {

            throw new DuplicateUserException(email);
        }

        user.update(
            request.getUsername(),
            email
        );

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new UserNotFoundException(id)
            );

        userRepository.delete(user);
    }

    private UserResponse toResponse(User user) {

        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}