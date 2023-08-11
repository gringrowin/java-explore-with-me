package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createByPrivate(@PathVariable Long userId,
                                      @RequestParam Long eventId,
                                      @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentPrivateController.createByPrivate: {}, {}, {}  - Started", userId, eventId, newCommentDto);
        CommentDto commentDto = commentService.createByPrivate(userId, eventId, newCommentDto);
        log.info("CommentPrivateController.createByPrivate: {} - Finished", commentDto);
        return commentDto;
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateByPrivate(@PathVariable Long userId,
                                     @PathVariable Long commentId,
                                     @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("CommentPrivateController.updateByPrivate: {}, {}, {}  - Started", userId, commentId, newCommentDto);
        CommentDto commentDto = commentService.updateByPrivate(userId, commentId, newCommentDto);
        log.info("CommentPrivateController.updateByPrivate: {} - Finished", commentDto);
        return commentDto;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByPrivate(@PathVariable Long userId,
                                @PathVariable Long commentId) {
        log.info("CommentPrivateController.deleteByPrivate: {}, {} - Started", userId, commentId);
        commentService.deleteByPrivate(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByPrivate(
            @PathVariable Long userId,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("CommentPrivateController.updateByPrivate: {}, {}, {}  - Started", userId, from, size);
        List<CommentDto> commentDtoList = commentService.getCommentsByPrivate(userId, eventId, from, size);
        log.info("CommentPrivateController.updateByPrivate: {} - Finished", commentDtoList);
        return commentDtoList;
    }
}
