package com.soda2026.journi.user;

public class UserResponseDTO {

    private String name;

    public UserResponseDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
