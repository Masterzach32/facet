
repositories {
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    val lavaplayer_version: String by project

    api(core)
    api("com.sedmelluq:lavaplayer:$lavaplayer_version")
}

