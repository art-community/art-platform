package ru.art.platform.api.model.git;

import lombok.*;
import java.util.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class GitChanges {
    @Singular("delete")
    @EqualsAndHashCode.Exclude
    private final List<String> deleted;

    @Singular("modify")
    @EqualsAndHashCode.Exclude
    private final List<String> modified;

    @Singular("add")
    @EqualsAndHashCode.Exclude
    private final List<String> added;

    @Singular("rename")
    @EqualsAndHashCode.Exclude
    private final Map<String, String> renamed;

    @Singular("copy")
    @EqualsAndHashCode.Exclude
    private final Map<String, String> copied;
}
