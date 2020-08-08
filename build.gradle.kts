val kotlinx_coroutines_version: String by project

plugins {
    kotlin("jvm") version "1.3.72" apply false
    id("com.gorylenko.gradle-git-properties") version "2.2.2" apply false
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
    id("org.jetbrains.dokka") version "1.4.0-rc"
    `java-library`
    `maven-publish`
}

repositories {
    jcenter()
}

allprojects {
    group = "io.facet"
    version = getVersionFromSemver()
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinx_coroutines_version")
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from((project.the<SourceSetContainer>()["main"] as SourceSet).allSource)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenKotlin") {
                artifactId = project.name
                version = getVersionFromSemver()
                from(components["kotlin"])
                artifact(sourcesJar.get())
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }
            }
        }
    }
}

tasks {
    dokkaHtmlMultimodule {
        outputDirectory = "$buildDir/multimodule"
    }

    build {
        dependsOn(dokkaHtmlMultimodule)
    }
}

fun getVersionFromSemver() = file("version.properties")
    .readLines()
    .first { it.contains("version.semver") }
    .split("=")
    .last()
    .trim()
