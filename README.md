# testcontainers-jenkins
Testcontainers module for Jenkins

## Goal

Enable testing Jenkins environments that are closer to production (no development mode) with the tools of your choice.
Spin up a real Jenkins, run your init scripts, install your plugin set, and test away.

## Open Questions

### Dynamic Commands and Restart

The [official Jenkins docker image][image] includes a script for installing plugins.
Plugin installs sometimes need to be retried after failing, and may need to be restarted before working.

We have some layers to model:

    [user-supplied init/plugins] example: oss plugins, internal plugins, configure plugins
    [testcontainers init]        example: skip wizard, bootstrap users with api tokens
    [official jenkins image]

Is the best place for this in creating a custom image?

My first thought was to try this without a custom image, but seemed like it'd take much longer than a custom image:
1. wait for startup
2. copy files into container
3. exec in container
4. restart

For the testcontainers init layer, are there recommended patterns for copying a file from the classpath to the
container?

One option is to copy to a temp file, then have a COPY command in the image.

Each stage has several failure modes.
How to surface/monitor installation/startup errors in the container - [container to slf4j][slf4j]?


[image]: https://github.com/jenkinsci/docker/blob/master/README.md
[slf4j]: https://www.testcontainers.org/features/container_logs/#streaming-container-output-to-an-slf4j-logger

### Multiple Containers

Are there patterns for coordinating multiple containers from a single spec?

    jenkinsControllerBuilder()                               // container 1
    .addAgent(jenkinsAgentBuilder().name("agent-1").build()) // container 2
    .addAgent(jenkinsAgentBuilder().name("agent-2").build()) // container 3
    .build()
