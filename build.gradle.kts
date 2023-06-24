plugins {
    `java-library`
}

group = "net.sghill.testcontainers"

dependencies {
    compileOnlyApi(libs.auto.value.annotations)
    annotationProcessor(libs.auto.value.processor)
    api(platform(libs.testcontainers.bom))
    api(libs.testcontainers.core)
    implementation(platform(libs.jackson.bom))
    implementation(libs.jackson.databind)

    testCompileOnly(libs.auto.value.annotations)
    testAnnotationProcessor(libs.auto.value.processor)
    testImplementation(libs.assertj.core)
    testImplementation(libs.rest.assured)
    testImplementation(libs.junit4)
    testImplementation(libs.awaitility)
    testRuntimeOnly(libs.slf4j.simple)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
