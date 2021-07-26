# Facet

[![GitHub workflow status](https://img.shields.io/github/workflow/status/Masterzach32/facet/Java%20CI?style=for-the-badge)]()

Facet is a library to make writing Discord bots easier. Features include:
* Full coroutines support.
* A plugin system, with inspiration taken from [Ktor](https://ktor.io/) to manage code organization.
* DSL syntax for commands, messages, and bot setup.
* Many useful extension functions for Discord4J objects.

View the [KDocs](https://masterzach32.github.io/facet)

**This documentation is a WIP**

## Setup
Add these to `build.gradle.kts`:
```kotlin
repositories {
    maven("https://maven.masterzach32.net/artifactory/releases")
}

dependencies {
    implementation("io.facet:chat-commands:$version") // if using chat commands
    implementation("io.facet:application-commands:$version") // if using "slash" commands
    // you can use both at the same time!
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
        .setEnabledIntents(IntentSet.all())
        .setSharding(ShardingStrategy.recommended())
        // This is the important part, and allows installing "plugins" into the gateway in a declarative syntax.
        .withPlugins(GatewayDiscordClient::configure)
        .block()
}

suspend fun GatewayDiscordClient.configure(scope: CoroutineScope) {
    // simple custom feature that manages a database table with guildIds and their prefixes
    install(scope, GuildStorage)

    // this is a pre-written command dispatcher that can be found in the facet-discord4j-commands module
    install(scope, ChatCommands) {
        useDefaultHelpCommand = true

        commandPrefix { guildId: Snowflake? -> feature(GuildStorage).commandPrefixFor(guildId) }

        // how many commands can be executing at once
        commandConcurrency = 4

        registerCommands(
            HelloWorld
        )
    }

    // this is a pre-written command dispatcher that can be found in the facet-discord4j-application-commands module
    install(scope, ApplicationCommands) {
        // how many commands can be executing at once
        commandConcurrency = 4
        
        registerCommands(
            HelloWorld
        )
    }
}
```

## Coroutines and event listeners
Starting a new event listener is easy, all you need is a `CoroutineScope` and an instance of the gateway.
This launches a new coroutine that collects the events from the gateway using a Flow. Eventually this will use
context receivers once [implemented](https://github.com/Kotlin/KEEP/issues/259) into Kotlin.

```kotlin
// this: GatewayDiscordClient
// scope: CoroutineScope
listener<MessageCreateEvent>(scope) { event ->
    event.message.channel.await().createMessage("Hello!")
}
```

If needed, an actor variant exists which contains a `Channel`.
This uses the same `ActorScope` that the kotlinx.coroutines library provides.

```kotlin
// this: GatewayDiscordClient
// scope: CoroutineScope
actorListener<MessageCreateEvent>(scope) {
    for (event in channel)
        println(event.message.content)
}
```
