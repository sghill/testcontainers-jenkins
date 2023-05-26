package net.sghill.testcontainers.jenkins;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class JenkinsContainer extends GenericContainer<JenkinsContainer> {
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("jenkins/jenkins");
    private static final String DEFAULT_TAG = "lts-jdk11";
    public static final int PORT = 8080;

    public JenkinsContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME, DockerImageName.parse("jenkins/jenkins"));

        waitingFor(Wait.forLogMessage(".*Jenkins is fully up and running$", 1));

//        withCommand("-serverPort " + PORT);
        addExposedPorts(PORT);
    }

}
