package net.sghill.testcontainers.jenkins;

import org.hamcrest.core.StringContains;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
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
    @Ignore
    public void shouldInstallGivenPlugins() throws IOException {
        Map<String, String> p = new HashMap<>();
        p.put("ant", "");
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(JenkinsSpec.create(p))) {
            jenkinsServer.start();
            String logs = jenkinsServer.getLogs();
            Path adminPassword = Files.createTempFile("jenkins-admin-", ".txt");
            jenkinsServer.copyFileFromContainer("/var/jenkins_home/secrets/initialAdminPassword", adminPassword.toString());


            Integer mappedPort = jenkinsServer.getMappedPort(JenkinsContainer.PORT);

            String password = Files.readAllLines(adminPassword).get(0);
            given()
                    .auth()
                    .basic("admin", password)
                    .port(mappedPort)
                .get("/pluginManager/api/json?depth=1")
                .then()
                    .body(StringContains.containsString("ant"));

        }
    }
}
