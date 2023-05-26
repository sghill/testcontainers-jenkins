package net.sghill.testcontainers.jenkins;

import org.junit.Test;
import org.testcontainers.utility.DockerImageName;

import static io.restassured.RestAssured.given;

public class JenkinsContainerTest {
    @Test
    public void shouldCallRunningJenkins() throws Exception {
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(DockerImageName.parse("jenkins/jenkins:lts-jdk11"))) {
            jenkinsServer.start();

            Integer mappedPort = jenkinsServer.getMappedPort(JenkinsContainer.PORT);

            given()
                    .port(mappedPort).
            get("/").then()
                    .header("X-Jenkins", "2.387.3");

        }
    }
}
