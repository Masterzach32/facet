
repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    val brigadier_version: String by project

    api(project(":facet-d4j"))
    api("com.mojang:brigadier:$brigadier_version")
}

//interface ApplicationCommand {
//
//    fun register(): ApplicationCommandRequest
//
//    suspend fun execute(event: InteractionCreateEvent)
//}
