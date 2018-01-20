package xyz.cuteclouds.earthchan.core.listeners.operations

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.utils.TimeAmount

import java.util.concurrent.Future

class InteractiveOperation
internal constructor(
    internal val channelId: String,
    internal val initialTimeout: TimeAmount,
    internal val increasingTimeout: TimeAmount?,
    internal val onMessage: (GuildMessageReceivedEvent) -> Boolean,
    internal val onTimeout: (() -> Unit),
    internal val onRemoved: (() -> Unit)
) {
    internal var timeoutFuture: Future<*>? = null

    init {
        InteractiveOperations.register(this)
    }
}
