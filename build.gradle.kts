plugins {
    `java-library`
}

group = "net.sghill.testcontainers"

dependencies {
    compileOnlyApi(libs.auto.value.annotations)
    annotationProcessor(libs.auto.value)
    api("org.testcontainers:testcontainers:1.18.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")

    testCompileOnly(libs.auto.value.annotations)
    testAnnotationProcessor(libs.auto.value)
    testImplementation("org.assertj:assertj-core:3.+")
    testImplementation("io.rest-assured:rest-assured:5.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.awaitility:awaitility:4.+")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.36")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
