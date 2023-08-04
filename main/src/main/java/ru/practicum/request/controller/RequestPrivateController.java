package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
@Slf4j
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("RequestPrivateController.getRequests: {}  - Started", userId);
        List<ParticipationRequestDto> requestDtoList = requestService.getRequests(userId);
        log.info("RequestPrivateController.getRequests: {}  - Finished", requestDtoList.size());
        return requestDtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("RequestPrivateController.createRequest: {}, {}  - Started", userId, eventId);
        ParticipationRequestDto participationRequestDto = requestService.createRequest(userId, eventId);
        log.info("RequestPrivateController.createRequest: {}  - Finished", participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping(path = "/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto deleteRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("RequestPrivateController.deleteRequest: {}, {}  - Started", userId, requestId);
        ParticipationRequestDto participationRequestDto = requestService.cancelRequest(userId, requestId);
        log.info("RequestPrivateController.deleteRequest: {}  - Finished", participationRequestDto);
        return participationRequestDto;
    }
}
