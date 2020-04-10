
val brigadier_version: String by project

repositories {
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":core"))
    api("com.mojang:brigadier:$brigadier_version")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
