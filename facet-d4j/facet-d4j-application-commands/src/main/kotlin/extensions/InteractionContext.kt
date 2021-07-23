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

package io.facet.discord.appcommands.extensions

import io.facet.discord.appcommands.*
import io.facet.discord.extensions.*

/*
 * facet - Created on 6/6/2021
 * Author: Zach Kozar
 * 
 * This code is licensed under the GNU GPL v3
 * You can find more info in the LICENSE file at the project root.
 */

/**
 * @author Zach Kozar
 * @version 6/6/2021
 */

public suspend fun SlashCommandContext.acknowledge(ephemeral: Boolean = false): Unit =
    if (ephemeral)
        event.acknowledgeEphemeral().await()
    else
        event.acknowledge().await()

public suspend fun SlashCommandContext.acknowledgeEphemeral(): Unit = event.acknowledgeEphemeral().await()
