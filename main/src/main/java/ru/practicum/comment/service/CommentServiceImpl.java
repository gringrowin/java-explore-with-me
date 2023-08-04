package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ForbiddenParameterException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserService userService;

    private final EventService eventService;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getCommentsByAdmin(Integer from, Integer size) {
        List<Comment> comments = commentRepository.findAll(PageRequest.of(from / size, size)).toList();

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto createByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto) {

        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenParameterException("Коментировать возможно только опубликованные события");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto) {
        User user = userService.findUserById(userId);

        Comment comment = findCommentById(commentId);

        if (comment.getAuthor().equals(user)) {
            throw new ForbiddenParameterException("Редактировать возможно только собственные коментарии.");
        }
        comment.setText(newCommentDto.getText());
        comment.setEditedOn(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteByPrivate(Long userId, Long commentId) {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (comment.getAuthor().equals(user)) {
            throw new ForbiddenParameterException("Удалять возможно только собственные коментарии.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getCommentsByPrivate(Long userId, Long eventId, Integer from, Integer size) {
        userService.findUserById(userId);
        Pageable pageable =  PageRequest.of(from / size, size);

        List<Comment> comments;
        if (eventId != null) {
            eventService.findEventById(eventId);
            comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId, pageable);
        } else {
            comments = commentRepository.findAllByAuthorId(userId, pageable);
        }

        return comments
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByPublic(Long eventId, Integer from, Integer size) {
        eventService.findEventById(eventId);

        List<Comment> comments =
                commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size));
        return comments
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentByPublic(Long commentId) {
        return commentMapper.toCommentDto(findCommentById(commentId));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Подборки с ID : %s не найдено", commentId)));
    }
}
