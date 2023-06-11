package net.sghill.testcontainers.jenkins.spec;

import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
public abstract class UserSpec {
    public abstract String username();

    public static Builder userBuilder() {
        return new AutoValue_UserSpec.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder username(String username);

        abstract UserSpec autoBuild();

        public final UserSpec build() {
            UserSpec spec = autoBuild();
            String username = spec.username();
            if (username.isEmpty()) {
                throw new IllegalStateException("username is required");
            }
            return spec;
        }
    }
}
