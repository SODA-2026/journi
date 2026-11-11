 package com.soda2026.journi.user;

 import org.springframework.stereotype.Component;

 @Component
 public class UserMapper {

     public UserResponseDTO toDto(UserModel user){
         return new UserResponseDTO(
                 user.getName()
         );
     }

     public UserModel toEntity(UserRequestDTO userRequestDTO) {
         return new UserModel(
                 userRequestDTO.getEmail(),
                 userRequestDTO.getName(),
                 userRequestDTO.getPassword(),
                 userRequestDTO.getProfileImage()
                 );
     }
 }
