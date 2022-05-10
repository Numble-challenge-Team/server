package com.numble.shortForm.video.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class NormalVideoRequestDto {

    private String title;

    private MultipartFile video;

    private String description;

    private MultipartFile thumbnail;

    private List<String> tags;

    private Long duration;


}
