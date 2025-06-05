pluginManagement {
    repositories {
        maven {
            url = uri("http://94.156.170.35:4040/releases")
            isAllowInsecureProtocol = true
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "RepoMine"
include("quifft")
include("quifft")