package io.facet.core

import java.io.*
import java.util.*

/**
 * Provide information about the Git repository version captured at build time.
 */
public object GitProperties {
    public const val APPLICATION_NAME: String = "application.name"
    public const val APPLICATION_VERSION: String = "git.build.version"
    public const val APPLICATION_URL: String = "application.url"
    public const val GIT_COMMIT_ID_DESCRIBE: String = "git.commit.id.describe"

    /**
     * Load a [Properties] object with application version data.
     *
     * @return a property list with application version details
     */
    public val properties: Properties
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
