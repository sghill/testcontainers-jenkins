package net.sghill.testcontainers.jenkins;

import net.sghill.testcontainers.jenkins.gen.GeneratedUser;
import org.junit.Test;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class JenkinsContainerTest {
    @Test
    public void shouldCallRunningJenkins() {
        DockerImageName imageName = DockerImageName.parse("jenkins/jenkins:lts-jdk11");
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(imageName)) {
            jenkinsServer.start();

            Integer mappedPort = jenkinsServer.getMappedPort(JenkinsContainer.PORT);

            given()
                    .port(mappedPort).
                    get("/").then()
                    .header("X-Jenkins", startsWith("2."));

        }
    }

    @Test
    public void shouldInstallGivenPlugins() {
        Map<String, String> p = new HashMap<>();
        p.put("ant", "");
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(JenkinsSpec.create(p, "2.387.3"))) {
            jenkinsServer.start();
            Integer mappedPort = jenkinsServer.getMappedPort(JenkinsContainer.PORT);

            GeneratedUser ted = jenkinsServer.getUserByUsername("ted");

            given()
                .auth()
                    .preemptive()
                    .basic(ted.username(), ted.apiToken())
                .port(mappedPort)
            .when()
                .get("/pluginManager/api/json?depth=1")
            .then()
                .body("plugins.shortName", hasItem("ant"));

        }
    }
}
