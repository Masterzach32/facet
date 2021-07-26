
plugins {
    groovy
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(kotlin("gradle-plugin-api", version = "1.5.0"))
    implementation(gradleApi())
    implementation(localGroovy())
}