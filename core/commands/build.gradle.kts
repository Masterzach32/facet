
repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    val brigadier_version: String by project

    api(core)
    api("com.mojang:brigadier:$brigadier_version")
}
