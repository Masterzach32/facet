# Facet

Facet is a library to make writing Discord bots easier. Features include:
* Full coroutines support.
* A feature system, with inspiration taken from [Ktor](https://ktor.io/) to manage code organization.
* DSL syntax for commands, messages, and bot setup.
* Many useful extension functions for Discord4J objects.

**This documentation is a WIP**

## Setup
Add these to `build.gradle.kts`:
```kotlin
repositories {
    maven("https://maven.masterzach32.net")
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation("io.facet:facet-core")
    implementation("io.facet:facet-discord4j")
    implementation("io.facet:facet-discord4j-commands")
    implementation("io.facet:facet-discord4j-exposed")
}
```

Creating a new bot is easy:
```kotlin
fun main() {
    val client = DiscordClient.builder("<bot token>")
        // configure the DiscordClientBuilder here
        .build()

    client.gateway()
        // configure the Gateway here, for example
        //.setEnabledIntents(IntentSet.all())
        //.setSharding(ShardingStrategy.recommended())
        // This is the important part, and allows installing "features" into the gateway in a declarative syntax.
        .withFeatures(GatewayDiscordClient::configure)
        .block()
}

fun GatewayDiscordClient.configure() {
    // simple custom feature that manages a database table with guildIds and their prefixes
    install(GuildStorage)

    // this is a pre-written command dispatcher that can be found in the facet-discord4j-command module
    install(ChatCommands) {
        useDefaultHelpCommand = true

        commandPrefix { guildId: Snowflake? -> feature(GuildStorage).commandPrefixFor(guildId) }

        registerCommands(
            HelloWorld
        )
    }
}
```

## Coroutines and event listeners
Starting a new event listener is easy, all you need is a `CoroutineScope` and an instance of the gateway.
This launches a new coroutine that collects the events from the gateway using a Flow.
```kotlin
// this: CoroutineScope
// client: GatewayDiscordClient
listener<MessageCreateEvent>(client) { event ->
    event.message.channel.await().createMessage("Hello!")
}
```

If needed, an actor variant exists which contains a `Channel`.
 This uses the same `ActorScope` that the kotlinx.coroutines library provides.
```kotlin
BotScope.actorListener<MessageCreateEvent>(client) {
    for (event in channel)
        // handle MessageCreateEvent
}
```
