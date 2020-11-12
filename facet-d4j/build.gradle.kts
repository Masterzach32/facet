
val discord4j_version: String by project
val kotlinx_coroutines_version: String by project
val reactor_kotlin_extensions: String by project

allprojects {
    dependencies {
        implementation("com.discord4j:discord4j-core:$discord4j_version")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinx_coroutines_version")
        //implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactor_kotlin_extensions")
    }
}

dependencies {
    api(project(":facet-core"))
}
