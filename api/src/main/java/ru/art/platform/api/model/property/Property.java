package ru.art.platform.api.model.property;


import lombok.*;

@Value
@Builder(toBuilder=true)
@ToString(onlyExplicitlyIncluded = true)
public class Property {
    private final Long id;
    private final String name;
    private final String type;
    private final TextProperty textProperty;
    private final ResourceProperty resourceProperty;
}
