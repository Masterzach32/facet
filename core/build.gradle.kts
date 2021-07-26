
allprojects {
    repositories {
        //maven("https://maven.masterzach32.net/artifactory/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spring.io/milestone")
    }

    dependencies {
        val discord4j_version: String by project
        val kotlinx_coroutines_version: String by project
        val reactor_kotlin_extensions: String by project

        api("com.discord4j:discord4j-core:$discord4j_version")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinx_coroutines_version")
    }
}

dependencies {
    api(common)
}
