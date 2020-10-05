plugins {
    id("com.gorylenko.gradle-git-properties")
}

gitProperties {
    gitPropertiesDir = "${project.buildDir}/resources/main/io/discordcommons"
}
