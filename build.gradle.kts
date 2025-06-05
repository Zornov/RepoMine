import io.micronaut.gradle.docker.tasks.BuildLayersTask

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.micronaut.application)
    alias(libs.plugins.shadow)
}

group = "dev.zornov"
version = "1.0"

application {
    mainClass.set("dev.zornov.repomine.Main")
}

micronaut {
    processing {
        incremental.set(true)
        annotations.add("dev.zornov.repomine.*")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.plasmoverse.com/releases")
    maven("https://repo.plasmoverse.com/snapshots")
}

dependencies {
    // Micronaut core
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.runtime)
    kapt(libs.micronaut.inject.java)

    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // Minestom, Voice and logging
    implementation(libs.minestom)
    runtimeOnly(libs.logback.classic)
    implementation(project(":quifft"))
    compileOnly(libs.plasmovoice)
    implementation(libs.plasmovoice.minestom)
}

tasks.withType<BuildLayersTask> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.withType<Tar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.withType<Zip> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
