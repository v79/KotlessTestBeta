import io.kotless.plugin.gradle.dsl.kotless
import io.terraformkt.aws.data.route53.route53_delegation_set

plugins {
    kotlin("jvm") version "1.4.21"
    id("io.kotless") version "0.2.0-g" apply true
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
    implementation("io.kotless", "ktor-lang", "0.2.0-f")
    implementation("io.kotless", "ktor-lang-aws", "0.2.0-f")
    implementation("io.ktor:ktor-auth:1.5.0")
    implementation("io.ktor:ktor-auth-jwt:1.5.0")
    implementation("io.ktor:ktor-client-core:1.5.0")
    implementation("io.ktor:ktor-client-cio:1.5.0")
    implementation("io.ktor:ktor-client-serialization:1.5.0")
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
            prefix = "kotless-beta"
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
//        dns("kotlessbeta","liamjd.org")
    }

    extensions {
        local {
            useAWSEmulation = false
        }
    }
}
