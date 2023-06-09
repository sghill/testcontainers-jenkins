package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GeneratedAgent {
    public abstract String name();

    public abstract String secret();

    @JsonCreator
    public static GeneratedAgent create(@JsonProperty("name") String name, @JsonProperty("secret") String secret) {
        return new AutoValue_GeneratedAgent(name, secret);
    }
}
