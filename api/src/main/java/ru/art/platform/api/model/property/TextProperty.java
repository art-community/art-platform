package ru.art.platform.api.model.property;

import lombok.*;

@Value
@Builder(toBuilder=true)
@ToString(onlyExplicitlyIncluded = true)
public class TextProperty {
    private final String value;
}
