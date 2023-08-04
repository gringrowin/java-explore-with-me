package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByAdmin(Integer from, Integer size);

    void deleteByAdmin(Long commentId);

    CommentDto createByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteByPrivate(Long userId, Long commentId);

    List<CommentDto> getCommentsByPrivate(Long userId, Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsByPublic(Long eventId, Integer from, Integer size);

    CommentDto getCommentByPublic(Long commentId);
}
