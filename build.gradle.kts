plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.bk14er"
version = "1.0-SNAPSHOT"

val kotestVersion = "5.9.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}