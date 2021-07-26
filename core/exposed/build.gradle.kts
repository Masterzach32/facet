
dependencies {
    val exposed_version: String by project

    api(core)
    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-dao:$exposed_version")
}
