import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.8.21"
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("plugin.serialization") version "1.9.0"
}

group = "de.confusedalex"
version = "2.0"

val shadowImplementation by configurations.creating
configurations {
    val compileOnly by getting {
        extendsFrom(shadowImplementation)
        isCanBeResolved = true
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven ("https://jitpack.io" )
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    shadowImplementation("org.bstats:bstats-bukkit:3.0.2")
}

val relocateShadowJar = tasks.register<ConfigureShadowRelocation>("relocateShadowJar")
val shadowJarTask = tasks.named<ShadowJar>("shadowJar") {
    // Enable package relocation in resulting shadow jar
    relocateShadowJar.get().apply {
        prefix = "com.github.confusedalex.shadow"
        target = this@named
    }
    dependsOn(relocateShadowJar)
    minimize()
    archiveClassifier.set("")
    configurations = listOf(shadowImplementation)
}

// Add shadow jar to the Gradle module metadata api and runtime configurations
configurations {
    artifacts {
        runtimeElements(shadowJarTask)
        apiElements(shadowJarTask)
    }
}

tasks.whenTaskAdded {
    if (name == "publishPluginJar" || name == "generateMetadataFileForPluginMavenPublication") {
        dependsOn(tasks.named("shadowJar"))
    }
}

// Disabling default jar task as it is overridden by shadowJar
tasks.named("jar").configure {
    enabled = false
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("TheGoldEconomy")
}
