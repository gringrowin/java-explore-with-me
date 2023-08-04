package ru.practicum.error;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
}
