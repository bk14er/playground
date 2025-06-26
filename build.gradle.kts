import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.0.20"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "1.8.20"
    id("com.google.protobuf") version "0.9.4"
}

group = "com.bk14er"
version = "1.0-SNAPSHOT"

val kotestVersion = "5.9.1"

buildscript {
    plugins {
        id("hyperskill")
    }

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.jetbrains.team/maven/p/hyperskill-hs-test/maven")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("com.github.hyperskill:hs-test:release-SNAPSHOT")

    // GRPC
    implementation("io.grpc:grpc-kotlin-stub:1.4.1")
    implementation("io.grpc:grpc-stub:1.71.0")
    implementation("io.grpc:grpc-netty-shaded:1.71.0")
    implementation("io.grpc:grpc-protobuf:1.71.0")
    implementation("com.google.protobuf:protobuf-kotlin:4.30.2")
    // GRPC
    implementation("com.github.avro-kotlin.avro4k:avro4k-core:2.3.0")
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Token
    implementation("com.auth0:java-jwt:3.18.2")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("com.google.cloud:google-cloud-secretmanager:2.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("io.kotest:kotest-runner-junit5:6.0.0.M4")
    testImplementation("io.mockk:mockk:1.14.2")

}

tasks.test {
    systemProperty("file.encoding", "utf-8")
    outputs.upToDateWhen { false }
}
kotlin {
    jvmToolchain(17)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.2"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.71.0"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.4.1:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("kotlin")
            }
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}