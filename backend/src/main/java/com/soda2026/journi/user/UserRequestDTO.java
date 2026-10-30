package com.soda2026.journi.user;

public class UserRequestDTO {

    private String name;
    private String email;
    private String password;
    private String profileImage;


    public UserRequestDTO(String name, String email, String password, String profileImage) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public UserRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
