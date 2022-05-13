package com.numble.shortForm.report.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReportRequestDto {

    private Long videoId;

    private String context;

}
