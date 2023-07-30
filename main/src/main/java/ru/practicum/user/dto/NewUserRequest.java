package ru.practicum.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class NewUserRequest {

    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
