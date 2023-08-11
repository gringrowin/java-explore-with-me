package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Validated
@Slf4j
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByPublic(
            @RequestParam Long eventId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("CommentPublicController.getCommentsByPublic: {}, {}, {} - Started", eventId, from, size);
        List<CommentDto> commentDtoList = commentService.getCommentsByPublic(eventId, from, size);
        log.info("CommentPublicController.getCommentsByPublic: {} - Finished", commentDtoList);
        return commentDtoList;
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByPublic(@PathVariable Long commentId) {
        log.info("CommentPublicController.getCommentByPublic: {} - Started", commentId);
        CommentDto commentDto = commentService.getCommentByPublic(commentId);
        log.info("CommentPublicController.getCommentByPublic: {} - Finished", commentDto);
        return commentDto;
    }
}
