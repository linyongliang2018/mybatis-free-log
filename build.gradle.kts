plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intellijPlatform)
}

group = "com.swqsv"
version = "1.0.0"

// Set the JVM language level used to build the project. Aligned with IntelliJ 2024+; JDK 17+ is required at runtime.
kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion"))
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        bundledPlugin("com.intellij.java")
    }
    testImplementation("junit:junit:4.13.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = provider { null }
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
}
