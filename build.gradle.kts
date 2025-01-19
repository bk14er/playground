plugins {
    kotlin("jvm") version "2.0.20"
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
}

tasks.test {
    systemProperty("file.encoding", "utf-8")
    outputs.upToDateWhen { false }
}
kotlin {
    jvmToolchain(17)
}