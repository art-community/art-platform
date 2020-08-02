package ru.art.platform.api.model.subject;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class Subject {
    private final Long id;
    private final String kind;
}
