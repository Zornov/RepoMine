plugins {
    alias(libs.plugins.kotlin)
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
    // Micronaut Core
    implementation(libs.micronaut.inject)
    implementation(libs.micronaut.runtime)
    kapt(libs.micronaut.inject.java)

    // Game-related
    implementation(libs.minestom)
    implementation(libs.worldseed)

    // Logging
    runtimeOnly(libs.logback.classic)

    // Voicechat
    implementation(project(":quifft"))
    compileOnly(libs.plasmovoice)
    implementation(libs.plasmovoice.minestom)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
