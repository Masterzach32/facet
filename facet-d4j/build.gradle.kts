
val discord4j_version: String by project
val kotlinx_coroutines_version: String by project
val reactor_kotlin_extensions: String by project

allprojects {
    repositories {
        maven("https://maven.masterzach32.net/artifactory/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        implementation("com.discord4j:discord4j-core:$discord4j_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinx_coroutines_version")
        //implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactor_kotlin_extensions")
    }
}

dependencies {
    api(project(":facet-core"))
}