import org.jetbrains.dokka.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.net.*

plugins {
    kotlin("jvm") version "1.5.10" apply false
    id("java-library")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.5.0"
    id("net.researchgate.release") version "2.8.1"
}

allprojects {
    group = "io.facet"
    description = "A Kotlin-friendly wrapper for Discord4J"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        maven("https://libraries.minecraft.net")
        maven("https://m2.dv8tion.net/releases")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.7.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    tasks {
        val compileKotlin by existing(KotlinCompile::class)
        val compileTestKotlin by existing(KotlinCompile::class)

        compileKotlin {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = listOf(CompilerArguments.explicitApi, CompilerArguments.optIn)
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
    }

    val dokkaHtmlPartial by tasks.existing(DokkaTaskPartial::class) {
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

                val discord4jProjects = listOf(
                    "discord-json",
                    "discord4j-common",
                    "discord4j-core",
                    "discord4j-gateway",
                    "discord4j-rest",
                    "discord4j-voice"
                )
                discord4jProjects
                    .map { name -> "https://javadoc.io/doc/com.discord4j/$name/latest/" }
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

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(project.sourceSets["main"].allSource)
    }

    val dokkaJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(dokkaHtmlPartial)
    }

    tasks.assemble {
        dependsOn(sourcesJar, dokkaJar)
    }

    publishing {
        publications {
            create<MavenPublication>("kotlin") {
                from(components["kotlin"])
                artifact(sourcesJar)
                artifact(dokkaJar)
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }

                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set("Facet")
                    description.set(project.description)
                    url.set("https://github.com/Masterzach32/facet")
                    organization {
                        name.set("Facet")
                        url.set("https://github.com/Masterzach32/facet")
                    }
                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/Masterzach32/facet/issues")
                    }
                    licenses {
                        license {
                            name.set("AGPL-3.0")
                            url.set("https://github.com/Masterzach32/facet/blob/master/LICENSE")
                            distribution.set("repo")
                        }
                    }
                    scm {
                        url.set("https://github.com/Masterzach32/facet")
                        connection.set("scm:git:git://github.com/Masterzach32/facet.git")
                        developerConnection.set("scm:git:ssh://git@github.com:Masterzach32/facet.git")
                    }
                    developers {
                        developer {
                            name.set("Zach Kozar")
                            email.set("zachkozar@vt.edu")
                        }
                    }
                }
            }
        }

        repositories {
            if (isRelease) {
                val sonatypeUsername: String? by project
                val sonatypePassword: String? by project
                if (sonatypeUsername != null && sonatypePassword != null) {
                    maven {
                        name = "MavenCentral"
                        url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                            credentials {
                                username = sonatypeUsername
                                password = sonatypePassword
                            }
                    }
                }
            }

            val mavenUsername: String? by project
            val mavenPassword: String? by project
            if (mavenUsername != null && mavenPassword != null) {
                maven {
                    if (isRelease) {
                        name = "Releases"
                        url = uri("https://maven.masterzach32.net/artifactory/facet-releases/")
                    } else {
                        name = "Snapshots"
                        url = uri("https://maven.masterzach32.net/artifactory/facet-snapshots/")
                    }
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        }
    }

    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["kotlin"])
    }

    tasks.withType<Sign>().configureEach {
        onlyIf { isRelease }
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("docs"))
    }

    register("docs") {
        dependsOn(dokkaHtmlMultiModule)
        group = "documentation"
    }

    val updateReadme by registering {
        group = "release"
        onlyIf {
            isRelease && version.toString().let { "M" !in it && "RC" !in it }
        }
        doLast {
            copy {
                from(".github/README_TEMPLATE.md")
                into(".")
                rename { "README.md" }
                expand("version" to version)
            }
        }
    }

    preTagCommit {
        dependsOn(updateReadme)
    }
}

release {
    preTagCommitMessage = "Release version"
    tagCommitMessage = "Release version"
    newVersionCommitMessage = "Next development version"
}
