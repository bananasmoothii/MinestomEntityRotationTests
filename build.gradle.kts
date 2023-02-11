import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    //kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "fr.bananasmoothii"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    //testImplementation(kotlin("test"))

    implementation("com.github.Minestom", "Minestom", "-SNAPSHOT") {
        // exclude tinylog for log4j2
        exclude(group = "org.tinylog")
    }

    // logging
    implementation("org.apache.logging.log4j", "log4j-core", "2.18.0")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.18.0")
    implementation("org.apache.logging.log4j", "log4j-1.2-api", "2.18.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "fr.bananasmoothii.minestomentityrotationtests.Main"
        attributes["Multi-Release"] = "true"
    }
}


/*
tasks.test {
    useJUnitPlatform()
}
*/

/*

kotlin {
    jvmToolchain(17)
}
*/
