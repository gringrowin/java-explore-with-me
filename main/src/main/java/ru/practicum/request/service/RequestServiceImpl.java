package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ForbiddenParameterException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.enums.RequestStatusAction;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserService userService;
    private final EventService eventService;

    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        log.info("RequestServiceImpl.getRequests: {} - Started", userId);
        userService.findUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        requests.forEach(request -> requestDtoList.add(requestMapper.toParticipationRequestDto(request)));
        log.info("RequestServiceImpl.getRequests: {} - Finished", requestDtoList.size());
        return requestDtoList;
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("RequestServiceImpl.createRequest: {}, {}  - Started", userId, eventId);
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (event.getInitiator().equals(user)) {
            throw new ForbiddenParameterException("Нельзя создать запрос на собственное событие");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenParameterException("Нельзя создать запрос на неопубликованое событие");
        }
        if (event.getConfirmedRequests() != null
                && Long.valueOf(event.getParticipantLimit()) <= event.getConfirmedRequests()) {
            throw new ForbiddenParameterException("Достигнут лимит запросов на участие в событии");
        }
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ForbiddenParameterException("Нельза создать повторный запрос");
        }

        Request newRequest = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventService.updateConfirmedRequestsInEvent(event);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        newRequest = requestRepository.save(newRequest);
        ParticipationRequestDto participationRequestDto =
                requestMapper.toParticipationRequestDto(newRequest);

        log.info("RequestServiceImpl.createRequest: {}  - Finished", participationRequestDto);
        return  participationRequestDto;
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("RequestServiceImpl.cancelRequest: {}, {} - Started", userId, requestId);
        User user = userService.findUserById(userId);
        Request request = findRequestById(requestId);
        if (!request.getRequester().equals(user)) {
            throw new ForbiddenParameterException("Нельзя отменить чужой запрос");
        }
        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        ParticipationRequestDto requestDto = requestMapper.toParticipationRequestDto(request);
        log.info("RequestServiceImpl.cancelRequest: {}  - Finished", requestDto);
        return requestDto;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByEventInitiator(Long userId, Long eventId) {
        log.info("RequestServiceImpl.getEventRequestsByEventInitiator:" +
                " {}, {} - Started", userId, eventId);
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenParameterException("Нельзя просматривать запросы на чужое событие");
        }
        List<Request> requests = requestRepository.findAllByEventId(eventId);

        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        requests.forEach(request -> requestDtoList.add(requestMapper.toParticipationRequestDto(request)));

        log.info("RequestServiceImpl.getEventRequestsByEventInitiator: {}  - Finished", requestDtoList);
        return requestDtoList;
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchEventRequestsByEventInitiator(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("RequestServiceImpl.patchEventRequestsByEventInitiator:" +
                " {}, {}, {} - Started", userId, eventId, eventRequestStatusUpdateRequest);
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);
        if (!event.getInitiator().equals(user)) {
            throw new ForbiddenParameterException("Нельзя изменять запросы на чужое событие");
        }
        if (!event.getRequestModeration() ||
                event.getParticipantLimit() == 0 ||
                eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(Collections.emptyList(), Collections.emptyList());
        }

        List<Request> confirmedList = new ArrayList<>();
        List<Request> rejectedList = new ArrayList<>();

        List<Request> requests = requestRepository
                .findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (!requests.stream()
                .map(Request::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ForbiddenParameterException("Изменять только можно запросы в ожидании");
        }

        Long eventConfirmedRequest =
                event.getConfirmedRequests() == null ? 0 : event.getConfirmedRequests();

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
            requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            rejectedList.addAll(requestRepository.saveAll(requests));
        }
        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.CONFIRMED)) {
            requests.forEach(request -> {
                if (Long.valueOf(event.getParticipantLimit()) > eventConfirmedRequest) {
                    event.setConfirmedRequests(eventConfirmedRequest + 1);
                    request.setStatus(RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    confirmedList.add(request);
                } else {
                    throw new ForbiddenParameterException("Достигнут лимит запросов на участие в событии");
                }
            });
            eventService.updateConfirmedRequestsInEvent(event);
            confirmedList.addAll(requestRepository.saveAll(requests));
        }
        List<ParticipationRequestDto> confirmedRequestDtoList = new ArrayList<>();
        confirmedList.forEach(
                request -> confirmedRequestDtoList.add(requestMapper.toParticipationRequestDto(request)));

        List<ParticipationRequestDto> rejectedRequestDtoList = new ArrayList<>();
        rejectedList.forEach(
                request -> rejectedRequestDtoList.add(requestMapper.toParticipationRequestDto(request)));

        EventRequestStatusUpdateResult result =
                new EventRequestStatusUpdateResult(confirmedRequestDtoList, rejectedRequestDtoList);
        log.info("RequestServiceImpl.patchEventRequestsByEventInitiator: {}  - Finished", result);
        return result;
    }

    @Override
    public Request findRequestById(Long requestId) {
        log.info("RequestServiceImpl.findRequestById: {} ", requestId);
        return requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(
                        String.format("Запрос с ID : %s не найдено", requestId))
        );
    }
}
