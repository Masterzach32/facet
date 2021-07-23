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

/**
 * Acknowledges the interaction indicating a response will be edited later. The user sees a loading state,
 * visible to all participants in the invoking channel. For a "only you can see this" response, set [ephemeral] to
 * `true`, or use acknowledgeEphemeral().
 */
public suspend fun SlashCommandContext.acknowledge(ephemeral: Boolean = false): Unit =
    if (ephemeral)
        event.acknowledgeEphemeral().await()
    else
        event.acknowledge().await()

/**
 * Acknowledges the interaction indicating a response will be edited later. Only the invoking user sees a loading state.
 */
public suspend fun SlashCommandContext.acknowledgeEphemeral(): Unit = event.acknowledgeEphemeral().await()
