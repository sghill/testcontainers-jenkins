package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GeneratedUser {

    @JsonProperty("username")
    public abstract String username();

    @JsonProperty("password")
    public abstract String password();

    @JsonProperty("apiToken")
    public abstract String apiToken(); // TODO make this a map to better reflect the domain

    @JsonCreator
    public static GeneratedUser create(@JsonProperty("username") String username,
                                       @JsonProperty("password") String password,
                                       @JsonProperty("apiToken") String apiToken) {
        return new AutoValue_GeneratedUser(username, password, apiToken);
    }
}
