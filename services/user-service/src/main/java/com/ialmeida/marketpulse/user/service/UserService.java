package com.ialmeida.marketpulse.user.service;

import com.ialmeida.marketpulse.user.dto.CreateUserRequest;
import com.ialmeida.marketpulse.user.dto.UpdateUserRequest;
import com.ialmeida.marketpulse.user.dto.UserResponse;
import com.ialmeida.marketpulse.user.model.User;
import com.ialmeida.marketpulse.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "A user with this email already exists."
            );
        }

        User user = new User(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    public List<UserResponse> getUsers() {

        return userRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public UserResponse getUser(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "User with id " + id + " not found."
                )
            );

        return toResponse(user);
    }

    public UserResponse updateUser(
        Long id,
        UpdateUserRequest request
    ) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "User with id " + id + " not found."
                )
            );

        if (!user.getEmail().equals(request.getEmail())
            && userRepository.existsByEmail(request.getEmail())) {

            throw new IllegalArgumentException(
                "A user with this email already exists."
            );
        }

        user.update(
            request.getUsername(),
            request.getEmail()
        );

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "User with id " + id + " not found."
                )
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
}