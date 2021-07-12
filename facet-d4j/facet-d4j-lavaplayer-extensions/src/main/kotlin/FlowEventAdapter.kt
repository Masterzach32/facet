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

import com.sedmelluq.discord.lavaplayer.player.event.*
import kotlinx.coroutines.flow.*

public class FlowEventAdapter : AudioEventAdapter() {

    private val sharedFlow = MutableSharedFlow<AudioEvent>()

    public val eventFlow: SharedFlow<AudioEvent> = sharedFlow.asSharedFlow()

    public inline fun <reified E : AudioEvent> of(): Flow<E> = eventFlow.filterIsInstance()

    override fun onEvent(event: AudioEvent) {
        sharedFlow.tryEmit(event)
    }
}
