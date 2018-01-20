package xyz.cuteclouds.earthchan.utils.helpers

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.listeners.operations.InteractiveOperations
import xyz.cuteclouds.earthchan.utils.TimeAmount

import java.util.concurrent.*

class IntegerSelector(private val event: GuildMessageReceivedEvent) {
    private var increasingTimeout: TimeAmount? = null
    private var initialTimeout: TimeAmount? = null
    private var min = 0
    private var max = Integer.MAX_VALUE

    fun build(): Future<Int> {
        val result = CompletableFuture<Int>()
        InteractiveOperations.builder()
            .channel(event.channel.id)
            .initialTimeout(initialTimeout)
            .increasingTimeout(increasingTimeout)
            .onMessage { e ->
                if (e.author != event.author) return@onMessage false

                try {
                    val choose = Integer.parseInt(e.message.contentDisplay)

                    if (choose < min || choose > max) return@onMessage false

                    e.message.delete().queue()
                    result.complete(choose)

                    return@onMessage true
                } catch (ignored: Exception) {
                }

                false
            }
            .onRemoved { result.completeExceptionally(CancellationException()) }
            .onTimeout { result.completeExceptionally(TimeoutException()) }
            .forceCreate()
        return result
    }

    fun increasingTimeout(amount: Long, unit: TimeUnit): IntegerSelector {
        this.increasingTimeout = TimeAmount(amount, unit)
        return this
    }

    fun increasingTimeout(increasingTimeout: TimeAmount): IntegerSelector {
        this.increasingTimeout = increasingTimeout
        return this
    }

    fun initialTimeout(amount: Long, unit: TimeUnit): IntegerSelector {
        this.initialTimeout = TimeAmount(amount, unit)
        return this
    }

    fun initialTimeout(initialTimeout: TimeAmount): IntegerSelector {
        this.initialTimeout = initialTimeout
        return this
    }

    fun max(max: Int): IntegerSelector {
        this.max = max
        return this
    }

    fun min(min: Int): IntegerSelector {
        this.min = min
        return this
    }

    fun timeout(amount: Long, unit: TimeUnit): IntegerSelector {
        this.initialTimeout = TimeAmount(amount, unit)
        this.increasingTimeout = TimeAmount(amount, unit)
        return this
    }
}
