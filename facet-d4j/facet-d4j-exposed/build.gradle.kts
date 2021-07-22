
dependencies {
    val exposed_version: String by project

    api(project(":facet-d4j"))
    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-dao:$exposed_version")
}
