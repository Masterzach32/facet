
val discord4j_version: String by project
val lavaplayer_version: String by project

repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":facet-d4j"))
    implementation("com.sedmelluq:lavaplayer:$lavaplayer_version")
}

