import io.kotless.plugin.gradle.dsl.kotless

plugins {
    kotlin("jvm") version "1.4.21"
    id("io.kotless") version "0.2.0-e" apply true
}

group = "org.liamjd.kotless"
version = "0.1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.kotless", "ktor-lang", "0.2.0-c")
    implementation("io.kotless", "ktor-lang-aws", "0.2.0-c")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf()
        jvmTarget = "11"
    }
}

kotless {
    config {
        aws {
            storage {
                bucket = "kotless.liamjd.org"
            }
            terraform {
                profile = "default" // IAM AWS profile name
                region = "eu-west-2"
            }
        }
    }

    webapp {

    }

    extensions {
        local {
            useAWSEmulation = false
        }
    }
}
