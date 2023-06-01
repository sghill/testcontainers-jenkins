package net.sghill.testcontainers.jenkins.spec;

import com.google.auto.value.AutoValue;

import java.util.Optional;

@AutoValue
public abstract class PluginSpec {
    public abstract String artifactId();

    public abstract Optional<String> version();

    public static Builder pluginBuilder() {
        return new AutoValue_PluginSpec.Builder();
    }

    public String toNotation() {
        String v = version().orElse(null);
        if (v == null) {
            return artifactId();
        }
        return String.join(":", artifactId(), v);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder artifactId(String artifactId);

        public abstract Builder version(String version);

        abstract PluginSpec autoBuild();

        public final PluginSpec build() {
            PluginSpec spec = autoBuild();
            String artifactId = spec.artifactId();
            if (artifactId == null || artifactId.isEmpty()) {
                throw new IllegalStateException("artifactId is required");
            }
            return spec;
        }
    }
}
