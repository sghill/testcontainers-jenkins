package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class GeneratedInfo {
    public abstract Set<GeneratedAgent> agents();

    public abstract Set<GeneratedUser> users();

    public abstract String instanceIdentity();

    @JsonCreator
    public static GeneratedInfo create(@JsonProperty("users") Set<GeneratedUser> users,
                                       @JsonProperty("agents") Set<GeneratedAgent> agents,
                                       @JsonProperty("instanceIdentity") String instanceIdentity) {
        return new AutoValue_GeneratedInfo(agents, users, instanceIdentity);
    }
}
