package com.numble.shortForm.trace;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FieldLogTrace implements LogTrace{

    private static final String START_PREFIX ="-->";
    private static final String COMPLETE_PREFIX="<--";
    private static final String EX_PREFIX ="<X-";

    // traceId 동기화,동시성 이슈 발생
    private ThreadLocal<TraceId> traceIdHolder =new ThreadLocal<>();
    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();

        Long startTimeMs = System.currentTimeMillis();
        log.info("[{}] {}{}",traceId.getId(),addSpace(START_PREFIX,traceId.getLevel()),message );
        return new TraceStatus(traceId,startTimeMs,message);
    }

    private void syncTraceId() {
        if (traceIdHolder.get() == null) {
            traceIdHolder.set(new TraceId());
        }else {
            traceIdHolder.set(traceIdHolder.get().createNextId());
        }
    }

    @Override
    public void end(TraceStatus status) {

    }

    @Override
    public void exception(TraceStatus status, Exception e) {

    }

    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[" + traceId.getId() + "] " + addSpace(COMPLETE_PREFIX,
                    traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms");
        } else {
            log.info("[" + traceId.getId() + "] " + addSpace(EX_PREFIX,
                    traceId.getLevel()) + status.getMessage() + " time=" + resultTimeMs + "ms" +
                    " ex=" + e);

        }
        releaseTraceId();
    }

    private void releaseTraceId() {
        if (traceIdHolder.get().isFirstLevel()) {
            traceIdHolder.remove();
        }else{
            traceIdHolder.set(traceIdHolder.get().createPreviousId());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append( (i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
