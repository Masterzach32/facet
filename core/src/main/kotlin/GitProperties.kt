package io.discordcommons.core

import java.io.IOException
import java.util.*


/**
 * Provide information about the Git repository version captured at build time.
 */
object GitProperties {
    const val APPLICATION_NAME = "application.name"
    const val APPLICATION_VERSION = "git.build.version"
    const val APPLICATION_URL = "application.url"
    const val GIT_COMMIT_ID_DESCRIBE = "git.commit.id.describe"

    /**
     * Load a [Properties] object with application version data.
     *
     * @return a property list with application version details
     */
    val properties: Properties
        get() {
            val properties = Properties()
            try {
                GitProperties::class.java.getResourceAsStream("git.properties").use { inputStream ->
                    if (inputStream != null) {
                        properties.load(inputStream)
                    }
                }
            } catch (ignore: IOException) {
            }
            return properties
        }
}
