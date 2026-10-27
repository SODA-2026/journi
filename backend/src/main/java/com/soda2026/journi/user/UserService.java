package com.soda2026.journi.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final com.soda2026.journi.user.UserRepository userRepository;
    private final com.soda2026.journi.user.UserMapper userMapper;

    public UserService(com.soda2026.journi.user.UserRepository userRepository, com.soda2026.journi.user.UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<com.soda2026.journi.user.UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }
}
