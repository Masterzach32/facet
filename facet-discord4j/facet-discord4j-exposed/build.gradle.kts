
val exposed_version: String by project

repositories {
    mavenCentral()
}

dependencies {
    api(project(":facet-discord4j"))
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
}
