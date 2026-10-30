package com.soda2026.journi.user;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserResponseDTO toDto(UserModel user){
        return new UserResponseDTO(
                user.getName()
        );
    }

    public UserModel toEntity(UserRequestDTO userRequestDTO) {
        return new UserModel(
                LocalDateTime.now(),
                userRequestDTO.getEmail(),
                LocalDateTime.now(),
                userRequestDTO.getName(),
                userRequestDTO.getPassword(),
                userRequestDTO.getProfileImage(),
                LocalDateTime.now()
                );
    }
}
