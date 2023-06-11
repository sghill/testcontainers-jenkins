package net.sghill.testcontainers.jenkins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sghill.testcontainers.jenkins.gen.GeneratedAgent;
import net.sghill.testcontainers.jenkins.gen.GeneratedInfo;
import net.sghill.testcontainers.jenkins.gen.GeneratedUser;
import net.sghill.testcontainers.jenkins.input.AgentRequest;
import net.sghill.testcontainers.jenkins.input.ApiTokenRequest;
import net.sghill.testcontainers.jenkins.input.ProvisioningRequest;
import net.sghill.testcontainers.jenkins.input.UserRequest;
import net.sghill.testcontainers.jenkins.spec.JenkinsSpec;
import net.sghill.testcontainers.jenkins.spec.PluginSpec;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

public class JenkinsContainer extends GenericContainer<JenkinsContainer> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("jenkins/jenkins");
    public static final int PORT = 8080;
    public static final int INBOUND_AGENT_PORT = 50_000;
    private static final String STARTED_LOG_LINE = ".*Jenkins is fully up and running\n";

    private GeneratedInfo _info;

    public JenkinsContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME, DockerImageName.parse("jenkins/jenkins"));
        waitingFor(Wait.forLogMessage(STARTED_LOG_LINE, 1));
        addExposedPorts(PORT, INBOUND_AGENT_PORT);
    }

    public JenkinsContainer(JenkinsSpec spec) {
        super(new ImageFromDockerfile()
                .withFileFromTransferable("jenkins-version", Transferable.of("2.387.2", 0100666))
                .withFileFromTransferable("tc-input.json", Transferable.of(configFrom(spec)))
                .withFileFromClasspath("startup.groovy", "startup.groovy")
                .withDockerfileFromBuilder(b ->
                b
                        .from(spec.image() + ":" + spec.version())
                        .copy("jenkins-version", "/var/jenkins_home/jenkins.install.InstallUtil.lastExecVersion")
                        .copy("jenkins-version", "/var/jenkins_home/jenkins.install.UpgradeWizard.state") // TODO owned by root
                        .copy("tc-input.json", "/var/jenkins_home/.tc.in.json")
                        .copy("startup.groovy", "/usr/share/jenkins/ref/init.groovy.d/startup.groovy")
                        .run("jenkins-plugin-cli --plugins " + spec.plugins().stream().map(PluginSpec::toNotation).collect(joining(" ")))
                        .build()));
        waitingFor(Wait.forLogMessage(STARTED_LOG_LINE, 1));
        addExposedPorts(PORT, INBOUND_AGENT_PORT);
    }

    private static byte[] configFrom(JenkinsSpec spec) {
        try {
            return new ObjectMapper().writeValueAsBytes(
                    ProvisioningRequest.create(
                            spec.users().stream().map(u -> UserRequest.create(u.username(), singleton(ApiTokenRequest.create("token1")))).collect(toSet()), 
                            Stream.of(AgentRequest.create("agent-1")).collect(toSet()))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public GeneratedUser getUserByUsername(String username) {
        GeneratedInfo info = info();
        for (GeneratedUser user : info.users()) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public GeneratedAgent getAgentByName(String name) {
        GeneratedInfo info = info();
        for (GeneratedAgent agent : info.agents()) {
            if (agent.name().equals(name)) {
                return agent;
            }
        }
        return null;
    }
    
    public String getInstanceIdentity() {
        return info().getInstanceIdentity();
    }

    private GeneratedInfo info() {
        if (_info != null) {
            return _info;
        }
        return _info = copyFileFromContainer("/var/jenkins_home/.tc.json", inputStream ->
                new ObjectMapper().readValue(inputStream, GeneratedInfo.class));
    }
}
