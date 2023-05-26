plugins {
    `java-library`
}

group = "net.sghill.testcontainers"

dependencies {
    api("org.testcontainers:testcontainers:1.18.1")

    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("junit:junit:4.13.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
