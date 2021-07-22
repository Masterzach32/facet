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
    id("signing")
    id("org.jetbrains.dokka") version "1.5.0"
    id("net.researchgate.release") version "2.8.1"
}

allprojects {
    group = "io.facet"
    description = "A Kotlin-friendly wrapper for Discord4J"
    extra["isRelease"] = !version.toString().endsWith("-SNAPSHOT")

    repositories {
        mavenCentral()
    }
}

val isRelease: Boolean by extra

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")

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
    }

    val dokkaHtmlPartial by tasks.getting(DokkaTaskPartial::class) {
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

    val sourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(project.sourceSets["main"].allSource)
    }

    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from(dokkaHtmlPartial)
    }

    tasks.assemble {
        dependsOn(sourcesJar, javadocJar)
    }

    publishing {
        publications {
            create<MavenPublication>("facet") {
                from(components["kotlin"])
                artifact(sourcesJar)
                artifact(javadocJar)
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
        sign(publishing.publications["facet"])
    }

    tasks.withType<Sign>().configureEach {
        onlyIf { isRelease }
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(buildDir.resolve("docs"))
    }

    create("docs") {
        dependsOn(dokkaHtmlMultiModule)
        group = "documentation"
    }

    val updateReadme by creating {
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
