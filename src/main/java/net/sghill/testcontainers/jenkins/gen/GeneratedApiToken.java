package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GeneratedApiToken {
    public abstract String name();

    public abstract String token();

    @JsonCreator
    public static GeneratedApiToken create(@JsonProperty("name") String name, @JsonProperty("token") String token) {
        return new AutoValue_GeneratedApiToken(name, token);
    }
}
