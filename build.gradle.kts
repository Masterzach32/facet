import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.net.*

val slf4j_version: String by project
val logback_version: String by project
val kotlinx_coroutines_version: String by project

plugins {
    kotlin("jvm") version "1.5.10" apply false
    id("java-library")
    id("maven-publish")
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

        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")

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
                                    "${projectDir.toRelativeString(rootDir)}/src/main/kotlin"
                            )
                        )
                        remoteLineSuffix.set("#L")
                    }

                    val discord4j_docs_version: String by project
                    val discord_json_docs_version: String by project
                    val discord4jProjects = listOf(
                        "discord-json" to discord_json_docs_version,
                        "discord4j-common" to discord4j_docs_version,
                        "discord4j-core" to discord4j_docs_version,
                        "discord4j-gateway" to discord4j_docs_version,
                        "discord4j-rest" to discord4j_docs_version,
                        "discord4j-voice" to discord4j_docs_version
                    )
                    discord4jProjects
                        .map { (name, version) -> "https://javadoc.io/static/com.discord4j/$name/$version/" }
                        .forEach { url ->
                            externalDocumentationLink(url, "${url}element-list")
                        }

                    val externalLibDocs = listOf(
                        "https://projectreactor.io/docs/core/release/api/",
                        "https://kotlin.github.io/kotlinx.coroutines/"
                    )

                    externalLibDocs.forEach { url ->
                        externalDocumentationLink(url)
                    }
                }
            }
        }
    }

    java {
        //withJavadocJar()
        withSourcesJar()
    }

    publishing {
        publications {
            create<MavenPublication>("facet") {
                groupId = "io.facet"
                artifactId = project.name
                version = project.version.toString()
                from(components["java"])
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
