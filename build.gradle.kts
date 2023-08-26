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

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven ("https://jitpack.io" )
    maven {
        name = "jeffMediaPublic"
        url = uri("https://repo.jeff-media.com/public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("TheGoldEconomy")
}
