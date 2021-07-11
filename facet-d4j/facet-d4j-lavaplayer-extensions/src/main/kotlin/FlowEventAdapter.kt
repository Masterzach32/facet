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
