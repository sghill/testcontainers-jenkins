package net.sghill.testcontainers.jenkins.gen;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class GeneratedUser {

    public abstract String username();

    public abstract String password();
    
    public abstract Set<GeneratedApiToken> apiTokens();
    
    public GeneratedApiToken anyApiToken() {
        Set<GeneratedApiToken> tokens = apiTokens();
        if (tokens.isEmpty()) {
            return null;
        }
        return tokens.iterator().next();
    }

    @JsonCreator
    public static GeneratedUser create(@JsonProperty("name") String username,
                                       @JsonProperty("password") String password,
                                       @JsonProperty("apiTokens") Set<GeneratedApiToken> apiTokens) {
        return new AutoValue_GeneratedUser(username, password, apiTokens);
    }
}
