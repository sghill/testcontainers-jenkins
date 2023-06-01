package net.sghill.testcontainers.jenkins.spec;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PluginSpecTest {
    @Test
    public void shouldSerializeWithVersion() {
        PluginSpec plugin = PluginSpec.pluginBuilder()
                .artifactId("ant")
                .version("1.0")
                .build();
        String actual = plugin.toNotation();
        assertThat(actual).isEqualTo("ant:1.0");
    }

    @Test
    public void shouldSerializeWithoutVersion() {
        PluginSpec plugin = PluginSpec.pluginBuilder()
                .artifactId("ant")
                .build();
        String actual = plugin.toNotation();
        assertThat(actual).isEqualTo("ant");
    }

    @Test
    public void shouldThrowIfNoArtifactId() {
        assertThatThrownBy(() -> PluginSpec.pluginBuilder().build())
                .isInstanceOf(IllegalStateException.class);
    }
}
