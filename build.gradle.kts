import org.jetbrains.kotlin.gradle.tasks.*

val slf4j_version: String by project
val logback_version: String by project
val kotlinx_coroutines_version: String by project

plugins {
    kotlin("jvm") version "1.5.10" apply false
    //id("com.gorylenko.gradle-git-properties") version "2.2.2" apply false
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
    //id("org.jetbrains.dokka") version "1.4.10"
    `java-library`
    `maven-publish`
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
    }

    dependencies {
        implementation("org.slf4j:slf4j-api:$slf4j_version")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")

        testImplementation("ch.qos.logback:logback-classic:$logback_version")
    }

    tasks {
        val compileKotlin by existing(KotlinCompile::class)
        val compileTestKotlin by existing(KotlinCompile::class)

        compileKotlin {
            kotlinOptions.jvmTarget = "11"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "11"
        }
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from((project.the<SourceSetContainer>()["main"] as SourceSet).allSource)
    }

    publishing {
        repositories {
            if (project.hasProperty("maven_username")) {
                val maven_username: String by project
                val maven_pass: String by project
                maven {
                    name = "Dev"
                    url = uri("https://maven.masterzach32.net/artifactory/dev/")
                    credentials {
                        username = maven_username
                        password = maven_pass
                    }
                }

                maven {
                    name = "Release"
                    url = uri("https://maven.masterzach32.net/artifactory/release/")
                    credentials {
                        username = maven_username
                        password = maven_pass
                    }
                }
            }
        }

        publications {
            create<MavenPublication>("facet") {
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
//    dokkaHtmlMultiModule.configure {
//        outputDirectory.set(buildDir.resolve("dokkaMultiModule"))
//    }

//    build {
//        dependsOn(dokkaHtmlMultimodule)
//    }
}

fun getVersionFromSemver() = file("version.properties")
    .readLines()
    .first { it.contains("version.semver") }
    .split("=")
    .last()
    .trim()
