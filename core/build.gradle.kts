plugins {
    kotlin("jvm")
    id("com.gorylenko.gradle-git-properties")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

gitProperties {
    gitPropertiesDir = "${project.buildDir}/resources/main/io/discordcommons"
}
