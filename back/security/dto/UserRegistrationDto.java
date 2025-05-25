package com.laba.products.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDto {
//
//    @Email(message = "Please provide a valid email address.")
//    @NotBlank(message = "User should contains an E-mail!")
//    private String email;

//    @NotBlank(message = "User should contains a username!")
//    @Size(min = 8, message = "Username should have at least 8 characters!")
    private String username;

//    @NotBlank(message = "User should contains a password!")
//    @Size(min = 8, message = "Password should have at least 8 characters!")
    private String password;
}
