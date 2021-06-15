rootProject.name = "facet"
include(":facet-core")
include(":facet-d4j")
include(":facet-d4j:facet-d4j-commands")
include(":facet-d4j:facet-d4j-application-commands")
include(":facet-d4j:facet-d4j-exposed")
include(":facet-d4j:facet-d4j-lavaplayer-extensions")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
