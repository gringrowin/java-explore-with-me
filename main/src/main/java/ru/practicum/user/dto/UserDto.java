package ru.practicum.user.dto;

import lombok.*;

@Data
@RequiredArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;
}
