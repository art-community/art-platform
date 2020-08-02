package ru.art.platform.api.model.user;

import lombok.*;
import ru.art.generator.mapper.annotation.*;
import static java.time.Instant.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
@IgnoreGeneration
public class User {
    private final Long id;
    private final String name;
    private final String token;
    private final String fullName;
    private final byte[] password;
    private final String email;

    @Builder.Default
    private final Boolean admin = false;

    @Builder.Default
    private final Long updateTimeStamp = now().getEpochSecond();

    @Singular("availableProject")
    private final Set<Long> availableProjects;

    @Singular("availableAction")
    private final Set<String> availableActions;
}
