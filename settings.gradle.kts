plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "playground"

buildscript{
    repositories {
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/hyperskill-hs-test/maven")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }

    dependencies {
        classpath("com.github.hyperskill:hs-gradle-plugin:release-SNAPSHOT")
    }

    configurations.all {
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}