rootProject.name = "testcontainers-jenkins"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            val autoValue = version("auto-value") {
                strictly("[1.10.1, 2.0.0[")
            }
            library("auto-value", "com.google.auto.value", "auto-value").versionRef(autoValue)
            library("auto-value-annotations", "com.google.auto.value", "auto-value-annotations").versionRef(autoValue)
        }
    }
}
