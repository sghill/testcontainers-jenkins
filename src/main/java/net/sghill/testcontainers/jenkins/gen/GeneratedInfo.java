package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class GeneratedInfo {
    @JsonProperty("agents")
    public abstract Set<GeneratedAgent> agents();

    @JsonProperty("users")
    public abstract Set<GeneratedUser> users();

    @JsonCreator
    public static GeneratedInfo create(@JsonProperty("users") Set<GeneratedUser> users,
                                       @JsonProperty("agents") Set<GeneratedAgent> agents) {
        return new AutoValue_GeneratedInfo(agents, users);
    }
}
