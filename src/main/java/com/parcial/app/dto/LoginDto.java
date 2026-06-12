package com.parcial.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank @Email
    private String username;

    @NotBlank
    private String password;
}