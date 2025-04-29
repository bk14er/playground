plugins {
    kotlin("jvm") version "2.0.20"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "1.8.20"
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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

}

tasks.test {
    systemProperty("file.encoding", "utf-8")
    outputs.upToDateWhen { false }
}
kotlin {
    jvmToolchain(17)
}