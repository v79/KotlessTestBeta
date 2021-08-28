import io.kotless.plugin.gradle.dsl.kotless
import com.meiuwa.gradle.sass.SassTask

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.5.20"
    id("io.kotless") version "0.2.0-g" apply true
    id("com.meiuwa.gradle.sass") version "2.0.0"
}

group = "org.liamjd.kotless"
version = "0.1.0-SNAPSHOT"

val ktorVersion by extra("1.5.0")
val kotlinVersion by extra("1.4.21")
val kotlessVersion by extra("0.2.0-f")
val awsVersion by extra("2.17.20")

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.kotless:ktor-lang:$kotlessVersion")
    implementation("io.kotless:ktor-lang-aws:$kotlessVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    // amazon
    implementation("software.amazon.awssdk:s3:$awsVersion")

    // testing
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.21")
    implementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

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

        optimization  {
            mergeLambda = io.kotless.KotlessConfig.Optimization.MergeLambda.None
        }
    }

    webapp {
//        dns("kotlessbeta","liamjd.org")
    }

    extensions {
        local {
            useAWSEmulation = false
            port = 8081
        }
    }
}

application {
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

sass {
   executable = "sass"
    download {
        enabled = false
    }
}

tasks.named<SassTask>("sassCompile") {
    source = fileTree("src/main/resources/sass/")
    output = file("src/main/resources/static/css")
}

tasks.test {
    useJUnitPlatform()
}
