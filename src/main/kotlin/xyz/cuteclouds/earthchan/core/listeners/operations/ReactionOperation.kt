package xyz.cuteclouds.earthchan.core.listeners.operations

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import xyz.cuteclouds.earthchan.utils.TimeAmount
import java.util.concurrent.Future

class ReactionOperation
internal constructor(
    message: Message,
    internal val reactions: Collection<String>,
    internal val timeout: TimeAmount,
    internal val onReaction: ((MessageReactionAddEvent) -> Boolean),
    internal val onTimeout: (() -> Unit),
    internal val onRemoved: (() -> Unit),
    force: Boolean
) {
    internal val messageId: String = message.id
    internal var timeoutFuture: Future<*>? = null

    init {
        ReactionOperations.register(this, message, force)
    }
}
