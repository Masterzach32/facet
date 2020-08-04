
val discord4j_version: String by project

allprojects {
    dependencies {
        implementation("com.discord4j:discord4j-core:$discord4j_version")
    }
}

dependencies {
    api(project(":core"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
