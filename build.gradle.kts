import io.kotless.plugin.gradle.dsl.kotless

plugins {
    kotlin("jvm") version "1.4.21"
    id("io.kotless") version "0.1.7-beta-5" apply true
}

group = "org.liamjd.kotless"
version = "0.1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.kotless", "ktor-lang", "0.1.7-beta-5")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf()
        jvmTarget = "11"
    }
}

kotless {
    config {
       bucket = "kotless.liamjd.org"
        terraform {
            profile = "liam" // IAM AWS profile name
            region = "eu-west-2"
        }
    }

    extensions {
        local {
            useAWSEmulation = false
        }
    }
}
