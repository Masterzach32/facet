rootProject.name = "facet"
include(":facet-core")
include(":facet-discord4j")
include(":facet-discord4j:facet-discord4j-commands")
include(":facet-discord4j:facet-discord4j-exposed")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
