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

import org.gradle.api.*
import org.gradle.kotlin.dsl.*

val DependencyHandlerScope.common get() = project(":common")
val DependencyHandlerScope.core get() = project(":core")
val DependencyHandlerScope.`chat-commands` get() = project(":core:chat-commands")
val DependencyHandlerScope.`application-commands` get() = project(":core:application-commands")
val DependencyHandlerScope.exposed get() = project(":core:exposed")
val DependencyHandlerScope.`lavaplayer-extensions` get() = project(":core:lavaplayer-extensions")


val Project.isSnapshot: Boolean get() = version.toString().endsWith("-SNAPSHOT")
val Project.isRelease: Boolean get() = !isSnapshot
