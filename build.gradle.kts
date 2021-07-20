import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.net.*

val slf4j_version: String by project
val logback_version: String by project
val kotlinx_coroutines_version: String by project

plugins {
    kotlin("jvm") version "1.5.10" apply false
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka") version "1.5.0"
    id("net.researchgate.release") version "2.8.1"
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
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xexplicit-api=strict")
            }
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

        withType<DokkaTaskPartial>().configureEach {
            dokkaSourceSets {
                configureEach {
                    sourceLink {
                        localDirectory.set(file("src/main/kotlin"))
                        remoteUrl.set(
                            URL(
                                "https://github.com/masterzach32/facet/blob/master/" +
                                    "${project.projectDir.relativeTo(rootProject.projectDir).path}/src/main/kotlin"
                            )
                        )
                        remoteLineSuffix.set("#L")
                    }

                    val externalLibDocs = mutableListOf<String>()

                    val discord4jProjects = listOf(
                        "discord-json",
                        "discord4j-command",
                        "discord4j-common",
                        "discord4j-core",
                        "discord4j-gateway",
                        "discord4j-rest",
                        "discord4j-voice"
                    )
                    externalLibDocs.addAll(
                        discord4jProjects
                            .map { "https://javadoc.io/doc/com.discord4j/$it/latest/" }
                    )

                    externalLibDocs.forEach { url ->
                        externalDocumentationLink(url)
                    }
                }
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
//                versionMapping {
//                    usage("java-api") {
//                        fromResolutionOf("runtimeClasspath")
//                    }
//
//                    usage("java-runtime") {
//                        fromResolutionResult()
//                    }
//                }
            }
        }

        repositories {
            maven {
                if (isRelease) {
                    name = "Releases"
                    url = uri("https://maven.masterzach32.net/artifactory/facet-releases/")
                } else {
                    name = "Snapshots"
                    url = uri("https://maven.masterzach32.net/artifactory/facet-snapshots/")
                }
                val mavenUsername = findProperty("maven_username")?.toString()
                val mavenPassword = findProperty("maven_password")?.toString()
                if (mavenUsername != null && mavenPassword != null) {
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
        outputDirectory.set(buildDir.resolve("docs"))
    }

    val docs by creating {
        dependsOn(dokkaHtmlMultiModule)
        group = "documentation"
    }
}

release {
    preTagCommitMessage = "Release version"
    tagCommitMessage = "Release version"
    newVersionCommitMessage = "Next development version"
}
