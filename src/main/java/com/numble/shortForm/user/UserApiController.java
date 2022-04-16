package com.numble.shortForm.user;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController {

    @ApiOperation("test api 입니다.")
    @GetMapping("/test")
    public String testApi() {
        return "test완료";
    }
}
