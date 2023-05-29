package net.sghill.testcontainers.jenkins;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sghill.testcontainers.jenkins.gen.GeneratedAgent;
import net.sghill.testcontainers.jenkins.gen.GeneratedInfo;
import net.sghill.testcontainers.jenkins.gen.GeneratedUser;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class JenkinsContainer extends GenericContainer<JenkinsContainer> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("jenkins/jenkins");
    private static final String DEFAULT_TAG = "lts-jdk11";
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
        super(new ImageFromDockerfile().withDockerfile(Paths.get("src/test/resources/TestDockerfile")));
        // TODO why didn't this work?
//        super(new ImageFromDockerfile().withDockerfileFromBuilder(b ->
//                b
//                        .from(DEFAULT_IMAGE_NAME + ":" + DEFAULT_TAG)
//                        .run("jenkins-plugin-cli --plugins " + spec.installLine())
//                        .build()));
        waitingFor(Wait.forLogMessage(STARTED_LOG_LINE, 1));
        addExposedPorts(PORT, INBOUND_AGENT_PORT);
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

    private GeneratedInfo info() {
        if (_info != null) {
            return _info;
        }
        try {
            Path localDestination = Files.createTempFile("tc-jenkins-", ".json");
            copyFileFromContainer("/var/jenkins_home/.tc.json", localDestination.toString());
            _info = new ObjectMapper().readValue(localDestination.toFile(), GeneratedInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return _info;
    }
}
