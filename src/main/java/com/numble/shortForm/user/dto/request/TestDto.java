package com.numble.shortForm.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TestDto {

    private String description;
    private List<String> tags;

}
