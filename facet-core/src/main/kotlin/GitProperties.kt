/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
