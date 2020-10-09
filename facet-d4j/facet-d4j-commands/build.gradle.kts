
val discord4j_version: String by project
val brigadier_version: String by project

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    api(project(":facet-d4j"))
    api("com.mojang:brigadier:$brigadier_version")
}
