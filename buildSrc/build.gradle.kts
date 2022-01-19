plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin", version = "1.6.10"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
}
