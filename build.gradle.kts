plugins {
    base
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    alias(libs.plugins.release)
}

val isRelease = !version.toString().endsWith("-SNAPSHOT")

allprojects {
    group = "io.facet"
    description = "A Kotlin-friendly wrapper for Discord4J"

    repositories {
        mavenCentral()
    }
}

tasks {
    dokkaHtmlMultiModule {
        dependsOn(clean)
        outputDirectory.set(buildDir.resolve("docs"))
    }

    register("docs") {
        dependsOn(dokkaHtmlMultiModule)
        group = "documentation"
        description = "Generate the KDocs for this project."
    }

    val updateReadme by registering(Copy::class) {
        group = "release"
        description = "Update the version number in the README.md at the root of the project."
        onlyIf { isRelease && version.toString().let { "M" !in it && "RC" !in it } }
        from(".github/README_TEMPLATE.md")
        into(".")
        rename { "README.md" }
        expand("version" to version)
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
