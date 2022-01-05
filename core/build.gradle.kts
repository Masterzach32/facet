plugins {
    `facet-module`
}

dependencies {
    api(projects.common)
    api(projects.applicationCommands)
    api(projects.chatCommands)
    implementation(libs.slf4j)

    testImplementation(libs.logback)
}
