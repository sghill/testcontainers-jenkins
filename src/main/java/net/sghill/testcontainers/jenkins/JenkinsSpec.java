package net.sghill.testcontainers.jenkins;

import com.google.auto.value.AutoValue;

import java.util.Map;

@AutoValue
public abstract class JenkinsSpec {
    public abstract Map<String, String> updateCenterPlugins();

    public String installLine() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : updateCenterPlugins().entrySet()) {
            sb.append(e.getKey()).append(":").append(e.getValue()).append(" ");
        }
        return sb.toString();
    }

    public static JenkinsSpec create(Map<String, String> updateCenterPlugins) {
        return new AutoValue_JenkinsSpec(updateCenterPlugins);
    }
}
