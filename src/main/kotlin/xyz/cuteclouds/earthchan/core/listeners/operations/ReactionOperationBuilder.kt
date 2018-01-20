package xyz.cuteclouds.earthchan.core.listeners.operations

import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import xyz.cuteclouds.earthchan.utils.TimeAmount
import java.util.*
import java.util.concurrent.TimeUnit

class ReactionOperationBuilder {
    private val reactions = LinkedList<String>()
    private var message: Message? = null
    private var onReaction: ((MessageReactionAddEvent) -> Boolean)? = null
    private var onRemoved: (() -> Unit)? = null
    private var onTimeout: (() -> Unit)? = null
    private var timeout: TimeAmount? = null

    fun addReactions(vararg reactions: String): ReactionOperationBuilder {
        Collections.addAll(this.reactions, *reactions)
        return this
    }

    @JvmOverloads
    fun create(success: () -> Unit = { }, failure: (Exception) -> Unit = { }) {
        try {
            ReactionOperation(
                message!!,
                reactions,
                timeout!!,
                onReaction!!,
                onTimeout ?: {},
                onRemoved ?: {},
                false
            )

            success()
        } catch (e: Exception) {
            failure(e)
        }
    }

    fun forceCreate() {
        ReactionOperation(
            message!!,
            reactions,
            timeout!!,
            onReaction!!,
            onTimeout ?: {},
            onRemoved ?: {},
            true
        )
    }

    fun message(message: Message): ReactionOperationBuilder {
        this.message = message
        return this
    }

    fun onReaction(onReaction: (MessageReactionAddEvent) -> Boolean): ReactionOperationBuilder {
        this.onReaction = onReaction
        return this
    }

    fun onRemoved(onRemoved: (() -> Unit)?): ReactionOperationBuilder {
        this.onRemoved = onRemoved
        return this
    }

    fun onTimeout(onTimeout: (() -> Unit)?): ReactionOperationBuilder {
        this.onTimeout = onTimeout
        return this
    }

    fun timeout(amount: Long, unit: TimeUnit): ReactionOperationBuilder {
        this.timeout = TimeAmount(amount, unit)
        return this
    }
}
