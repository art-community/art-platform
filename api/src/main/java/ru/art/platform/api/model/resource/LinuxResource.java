package ru.art.platform.api.model.resource;

import lombok.*;

@Value
@Builder(toBuilder = true)
@ToString(onlyExplicitlyIncluded = true)
public class LinuxResource {
    private final String sshHost;
    private final String sshLogin;
    private final String sshPassword;
}
