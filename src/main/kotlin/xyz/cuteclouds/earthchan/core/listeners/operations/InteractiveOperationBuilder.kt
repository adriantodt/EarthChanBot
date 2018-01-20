package xyz.cuteclouds.earthchan.core.listeners.operations

import net.dv8tion.jda.core.entities.TextChannel
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.utils.TimeAmount
import java.util.concurrent.TimeUnit

class InteractiveOperationBuilder {
    private var channelId: String? = null
    private var increasingTimeout: TimeAmount? = null
    private var initialTimeout: TimeAmount? = null
    private var onMessage: ((GuildMessageReceivedEvent) -> Boolean)? = null
    private var onTimeout: (() -> Unit)? = null
    private var onRemoved: (() -> Unit)? = null

    fun channel(channelId: String): InteractiveOperationBuilder {
        this.channelId = channelId
        return this
    }

    fun channel(channel: TextChannel): InteractiveOperationBuilder {
        this.channelId = channel.id
        return this
    }

    @JvmOverloads
    fun create(
        success: () -> Unit = { },
        failure: (Exception) -> Unit = { e ->
            throw e as? RuntimeException ?: RuntimeException(e)
        }
    ) {
        try {
            InteractiveOperation(
                channelId!!,
                initialTimeout!!,
                increasingTimeout,
                onMessage!!,
                onTimeout ?: {},
                onRemoved ?: {}
            )

            success()
        } catch (e: Exception) {
            failure(e)
        }

    }

    fun forceCreate() {
        ReactionOperations.stopOperation(channelId!!)
        create()
    }

    fun increasingTimeout(amount: Long, unit: TimeUnit): InteractiveOperationBuilder {
        this.increasingTimeout = TimeAmount(amount, unit)
        return this
    }

    fun increasingTimeout(amount: TimeAmount?): InteractiveOperationBuilder {
        this.increasingTimeout = amount
        return this
    }

    fun initialTimeout(amount: Long, unit: TimeUnit): InteractiveOperationBuilder {
        this.initialTimeout = TimeAmount(amount, unit)
        return this
    }

    fun initialTimeout(amount: TimeAmount?): InteractiveOperationBuilder {
        this.initialTimeout = amount
        return this
    }

    fun onMessage(onMessage: (GuildMessageReceivedEvent) -> Boolean): InteractiveOperationBuilder {
        this.onMessage = onMessage
        return this
    }

    fun onRemoved(onRemoved: (() -> Unit)?): InteractiveOperationBuilder {
        this.onRemoved = onRemoved
        return this
    }

    fun onTimeout(onTimeout: (() -> Unit)?): InteractiveOperationBuilder {
        this.onTimeout = onTimeout
        return this
    }

    fun timeout(amount: Long, unit: TimeUnit): InteractiveOperationBuilder {
        this.initialTimeout = TimeAmount(amount, unit)
        this.increasingTimeout = TimeAmount(amount, unit)
        return this
    }
}
