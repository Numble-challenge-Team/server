package com.numble.shortForm.hashtag.controller;

import com.numble.shortForm.hashtag.service.HashTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hashtags/")
@RequiredArgsConstructor
@Slf4j
public class HashTagApiController {

    private final HashTagService hashTagService;
}
