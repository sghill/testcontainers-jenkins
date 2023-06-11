package net.sghill.testcontainers.jenkins.spec;

import com.google.auto.value.AutoValue;

import java.util.HashSet;
import java.util.Set;

@AutoValue
public abstract class JenkinsSpec {
    public abstract String image();

    public abstract String version();

    public abstract Set<PluginSpec> plugins();
    
    public abstract Set<UserSpec> users();

    public static Builder jenkinsBuilder() {
        return new AutoValue_JenkinsSpec.Builder()
                .image("jenkins/jenkins")
                .version("lts-jdk11")
                .plugins(new HashSet<>());
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder image(String image);

        public abstract Builder version(String version);

        public abstract Builder plugins(Set<PluginSpec> plugins);
        
        public abstract Builder users(Set<UserSpec> users);

        public abstract JenkinsSpec build();
    }
}
