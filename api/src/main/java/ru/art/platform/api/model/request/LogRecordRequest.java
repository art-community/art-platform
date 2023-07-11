package ru.art.platform.api.model.request;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class LogRecordRequest {
    private final Long logId;
    private final String record;
}
