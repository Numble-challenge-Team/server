package com.numble.shortForm.comment.controller;

import com.numble.shortForm.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments/")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
}
