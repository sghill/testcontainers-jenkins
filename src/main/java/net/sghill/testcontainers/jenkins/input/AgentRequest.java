package net.sghill.testcontainers.jenkins.input;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AgentRequest {
    public abstract String getName();
    public abstract String getWorkspaceDir();
    
    public static AgentRequest create(String name) {
        return create(name, "/workspace");
    }
    
    public static AgentRequest create(String name, String workspaceDir) {
        return new AutoValue_AgentRequest(name, workspaceDir);
    }
}
