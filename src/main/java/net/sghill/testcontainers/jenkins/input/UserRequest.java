package net.sghill.testcontainers.jenkins.input;

import com.google.auto.value.AutoValue;

import java.util.Set;

import static java.util.Collections.emptySet;

@AutoValue
public abstract class UserRequest {
    public abstract String getName();

    public abstract Set<ApiTokenRequest> getApiTokens();

    public static UserRequest create(String name) {
        return create(name, emptySet());
    }

    public static UserRequest create(String name, Set<ApiTokenRequest> apiTokens) {
        return new AutoValue_UserRequest(name, apiTokens);
    }
}
