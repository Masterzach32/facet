import com.sedmelluq.discord.lavaplayer.player.event.*
import kotlinx.coroutines.flow.*

class FlowEventAdapter : AudioEventAdapter() {

    private val sharedFlow = MutableSharedFlow<AudioEvent>()

    val eventFlow = sharedFlow.asSharedFlow()

    inline fun <reified E : AudioEvent> of(): Flow<E> = eventFlow.filterIsInstance()

    override fun onEvent(event: AudioEvent) {
        sharedFlow.tryEmit(event)
    }
}
