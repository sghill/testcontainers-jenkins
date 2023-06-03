package net.sghill.testcontainers.jenkins.input;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ApiTokenRequest {
    public abstract String getName();

    public static ApiTokenRequest create(String name) {
        return new AutoValue_ApiTokenRequest(name);
    }
}
