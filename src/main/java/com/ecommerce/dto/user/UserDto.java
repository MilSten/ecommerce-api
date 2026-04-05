package com.ecommerce.dto.user;

import com.ecommerce.dto.BaseDto;
import lombok.*;
import com.ecommerce.entity.user.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDto extends BaseDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UserRole role;
    private Boolean isActive;
}