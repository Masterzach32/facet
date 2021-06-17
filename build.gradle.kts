import org.jetbrains.kotlin.gradle.tasks.*

val slf4j_version: String by project
val logback_version: String by project
val kotlinx_coroutines_version: String by project

plugins {
    kotlin("jvm") version "1.5.10" apply false
    id("org.jetbrains.dokka") version "1.4.32"
    id("net.researchgate.release") version "2.8.1"
    `java-library`
    `maven-publish`
}

allprojects {
    group = "io.facet"
    description = "Discord bot framework using D4J and Kotlin"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    val isRelease = !version.toString().endsWith("-SNAPSHOT")

    dependencies {
        implementation("org.slf4j:slf4j-api:$slf4j_version")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")

        testImplementation("ch.qos.logback:logback-classic:$logback_version")
        testImplementation(platform("org.junit:junit-bom:5.7.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks {
        val compileKotlin by existing(KotlinCompile::class)
        val compileTestKotlin by existing(KotlinCompile::class)

        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

        test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from((project.the<SourceSetContainer>()["main"] as SourceSet).allSource)
    }

    publishing {
        publications {
            create<MavenPublication>("facet") {
                artifactId = project.name
                version = project.version.toString()
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

        repositories {
            maven {
                if (isRelease) {
                    name = "Release"
                    url = uri("https://maven.masterzach32.net/artifactory/release/")
                } else {
                    name = "Dev"
                    url = uri("https://maven.masterzach32.net/artifactory/dev/")
                }
                val mavenUsername = findProperty("maven_username")?.toString()
                val mavenPassword = findProperty("maven_password")?.toString()
                if (mavenPassword != null && mavenPassword != null) {
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        }
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("dokkaMultiModule"))
    }
}

release {
    preTagCommitMessage = "Release version"
    tagCommitMessage = "Release version"
    newVersionCommitMessage = "Next development version"
}
