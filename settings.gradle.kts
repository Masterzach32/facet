rootProject.name = "facet"
include(":facet-core")
include(":facet-d4j")
include(":facet-d4j:facet-d4j-commands")
include(":facet-d4j:facet-d4j-exposed")

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}
