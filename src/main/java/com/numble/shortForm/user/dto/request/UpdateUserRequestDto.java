package com.numble.shortForm.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserRequestDto {




    private String nickname=null;

    private MultipartFile img=null;



}
