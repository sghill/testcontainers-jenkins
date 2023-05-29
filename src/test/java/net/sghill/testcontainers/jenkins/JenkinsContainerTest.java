package net.sghill.testcontainers.jenkins;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import io.restassured.response.Response;
import net.sghill.testcontainers.jenkins.gen.GeneratedAgent;
import net.sghill.testcontainers.jenkins.gen.GeneratedUser;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static net.sghill.testcontainers.jenkins.JenkinsContainer.PORT;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;

public class JenkinsContainerTest {
    @Test
    public void shouldCallRunningJenkins() {
        DockerImageName imageName = DockerImageName.parse("jenkins/jenkins:lts-jdk11");
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(imageName)) {
            jenkinsServer.start();

            Integer mappedPort = jenkinsServer.getMappedPort(PORT);

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
        JenkinsSpec spec = JenkinsSpec.create(p, "2.387.3");
        try (JenkinsContainer jenkinsServer = new JenkinsContainer(spec)) {
            jenkinsServer.start();

            GeneratedUser ted = jenkinsServer.getUserByUsername("ted");

            given()
                .auth()
                    .preemptive()
                    .basic(ted.username(), ted.apiToken())
                .port(jenkinsServer.getMappedPort(PORT))
            .when()
                .get("/pluginManager/api/json?depth=1")
            .then()
                .body("plugins.shortName", hasItem("ant"));
        }
    }

    @Test
    public void shouldConnectAgent() {
        JenkinsSpec spec = JenkinsSpec.create(new HashMap<>(), "2.387.3");
        try (Network network = Network.newNetwork();
             JenkinsContainer jenkinsServer = new JenkinsContainer(spec).withNetwork(network).withNetworkAliases("jcontroller")) {
            jenkinsServer.start();
            GeneratedUser ted = jenkinsServer.getUserByUsername("ted");
            Response response = given()
                    .auth()
                    .preemptive()
                    .basic(ted.username(), ted.apiToken())
                    .port(jenkinsServer.getMappedPort(PORT))
                    .when()
                    .get("/api/"); // X-Instance-Identity is not in the json response
            String instanceIdentity = response.header("X-Instance-Identity");
            GeneratedAgent genAgent = jenkinsServer.getAgentByName("agent-1");
            AgentsResponse expected = AgentsResponse.create(Arrays.asList(
                    AgentResponse.create("Built-In Node", false, false),
                    AgentResponse.create("agent-1", true, false)
            ));

            try (GenericContainer<?> agent = new GenericContainer<>(DockerImageName.parse("jenkins/jnlp-agent-jdk11"))) {
                agent
                        .withNetwork(network)
                        .withCommand("-direct", "jcontroller:50000", "-instanceIdentity", instanceIdentity, "-workDir", "/workspace", genAgent.secret(), genAgent.name())
                        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(JenkinsContainer.class)))
                        .waitingFor(Wait.forLogMessage("^INFO: Connected\n", 1));
                agent.start();
                
                AgentsResponse actual = await().atMost(Duration.ofMinutes(1))
                        .until(() -> given()
                                .auth()
                                .preemptive()
                                .basic(ted.username(), ted.apiToken())
                                .port(jenkinsServer.getMappedPort(PORT))
                                .when()
                                .get("/computer/api/json")
                                .then()
                                .extract()
                                .as(AgentsResponse.class), a -> 
                                    a.agents().stream()
                                            .filter(n -> n.displayName().equals(genAgent.name()))
                                            .anyMatch(x -> !x.offline()));

                assertEquals(expected, actual);
            }
        }
    }

    @AutoValue
    @JsonIgnoreProperties(ignoreUnknown = true)
    static abstract class AgentResponse {
        @JsonProperty("displayName")
        public abstract String displayName();

        @JsonProperty("jnlpAgent")
        public abstract boolean jnlpAgent();

        @JsonProperty("offline")
        public abstract boolean offline();

        @JsonCreator
        public static AgentResponse create(@JsonProperty("displayName") String displayName,
                                           @JsonProperty("jnlpAgent") boolean jnlpAgent,
                                           @JsonProperty("offline") boolean offline) {
            return new AutoValue_JenkinsContainerTest_AgentResponse(displayName, jnlpAgent, offline);
        }
    }

    @AutoValue
    @JsonIgnoreProperties(ignoreUnknown = true)
    static abstract class AgentsResponse {
        @JsonProperty("computer")
        public abstract List<AgentResponse> agents();

        @JsonCreator
        public static AgentsResponse create(@JsonProperty("computer") List<AgentResponse> agents) {
            return new AutoValue_JenkinsContainerTest_AgentsResponse(agents);
        }
    }
}
