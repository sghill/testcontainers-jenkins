package net.sghill.testcontainers.jenkins;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;

public class JenkinsContainer extends GenericContainer<JenkinsContainer> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("jenkins/jenkins");
    private static final String DEFAULT_TAG = "lts-jdk11";
    public static final int PORT = 8080;
    public static final int INBOUND_AGENT_PORT = 50_000;
    private static final String STARTED_LOG_LINE = ".*Jenkins is fully up and running\n";

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
}
