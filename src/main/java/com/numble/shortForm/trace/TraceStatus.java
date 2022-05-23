package com.numble.shortForm.trace;

import lombok.Getter;
import org.aspectj.weaver.tools.Trace;

@Getter
public class TraceStatus {

    private TraceId traceId;

    private Long startTimeMs;

    private String message;

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }


}
