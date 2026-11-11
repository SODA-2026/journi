package com.soda2026.journi.user;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final com.soda2026.journi.user.UserRepository userRepository;
    private final com.soda2026.journi.user.UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<com.soda2026.journi.user.UserResponseDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserResponseDTO findByEmail(String email) {
        return userMapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required")));
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        if (userRepository.existsByEmail(userRequestDTO.getEmail().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }
        UserModel userModel = new UserModel(
                userRequestDTO.getEmail().trim(),
                userRequestDTO.getName(),
                passwordEncoder.encode(userRequestDTO.getPassword()),
                userRequestDTO.getProfileImage()
        );


        try {
            return userMapper.toDto(userRepository.saveAndFlush(userModel));
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            if (userRepository.existsByEmail(userModel.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
            }
            throw exception;
        }


    }
}
