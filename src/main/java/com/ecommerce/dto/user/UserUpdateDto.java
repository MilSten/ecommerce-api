package com.ecommerce.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    @Email(message = "Email should be valid")
    private String email;

    private String firstName;
    private String lastName;
    private String phone;
}