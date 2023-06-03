package net.sghill.testcontainers.jenkins.input;

import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class ProvisioningRequest {
    public abstract Set<UserRequest> getUsers();
    public abstract Set<AgentRequest> getAgents();
    
    public static ProvisioningRequest create(Set<UserRequest> users, Set<AgentRequest> agents) {
        return new AutoValue_ProvisioningRequest(users, agents);
    }
}
