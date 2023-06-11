# testcontainers-jenkins
Testcontainers module for Jenkins

## Goal

Enable testing Jenkins environments that are closer to production (no development mode) with the tools of your choice.
Spin up a real Jenkins, run your init scripts, install your plugin set, and test away.

## Open Questions

Should we allow bringing your own http client and serialization?
Effectively this would mean having to depend on multiple modules, but it would allow users to avoid classpath conflicts.

How to surface/monitor installation/startup errors in the container - [container to slf4j][slf4j]?

### Dynamic Commands and Restart

The [official Jenkins docker image][image] includes a script for installing plugins.
Plugin installs sometimes need to be retried after failing, and may need to be restarted before working.

We have some layers to model:

    [user-supplied init/plugins] example: oss plugins, internal plugins, configure plugins
    [testcontainers init]        example: skip wizard, bootstrap users with api tokens
    [official jenkins image]

Is the best place for this in creating a custom image?

Answer: heavyweight images are best created out-of-band and used in tests. The docker image builder is better used for
light overrides specific to certain tests.


[image]: https://github.com/jenkinsci/docker/blob/master/README.md
[slf4j]: https://www.testcontainers.org/features/container_logs/#streaming-container-output-to-an-slf4j-logger

### Multiple Containers

Are there patterns for coordinating multiple containers from a single spec?

    jenkinsControllerBuilder()                               // container 1
    .addAgent(jenkinsAgentBuilder().name("agent-1").build()) // container 2
    .addAgent(jenkinsAgentBuilder().name("agent-2").build()) // container 3
    .build()

So far the tests are spinning up multiple containers, but there could be room for pushing this into the JenkinsSpec.
