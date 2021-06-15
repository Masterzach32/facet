
val discord4j_version: String by project
val lavaplayer_version: String by project

repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    api(project(":facet-d4j"))
    implementation("com.discord4j:discord4j-core:$discord4j_version")
    implementation("com.sedmelluq:lavaplayer:$lavaplayer_version")
}

