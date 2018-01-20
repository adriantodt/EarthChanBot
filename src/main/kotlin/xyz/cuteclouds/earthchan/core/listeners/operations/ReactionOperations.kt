package xyz.cuteclouds.earthchan.core.listeners.operations

import br.com.brjdevs.java.utils.async.threads.builder.ThreadBuilder
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent
import xyz.cuteclouds.earthchan.core.listeners.OptimizedListener
import xyz.cuteclouds.earthchan.extensions.classOf
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.function.Consumer

object ReactionOperations : OptimizedListener<MessageReactionAddEvent>(classOf()) {
    private val EXECUTOR = Executors.newSingleThreadScheduledExecutor(
        ThreadBuilder().setName("ReactionOperation Executor")
    )

    private val OPERATIONS = ConcurrentHashMap<String, ReactionOperation>()

    override fun event(event: MessageReactionAddEvent) {
        if (event.reaction.isSelf) return

        val id = event.messageId
        val operation = OPERATIONS[id]

        if (operation != null && operation.onReaction(event)) {
            operation.timeoutFuture?.cancel(true)
            operation.timeoutFuture = null
            OPERATIONS.remove(id, operation)
        }
    }

    fun builder(): ReactionOperationBuilder {
        return ReactionOperationBuilder()
    }

    fun register(operation: ReactionOperation, message: Message, force: Boolean) {

        if (!force && OPERATIONS.containsKey(operation.messageId))
            throw IllegalStateException("Operation already happening at messageId")

        OPERATIONS.put(operation.messageId, operation)

        operation.timeoutFuture = EXECUTOR.schedule(
            {
                OPERATIONS.remove(operation.messageId, operation)
                operation.onTimeout()
            }, operation.timeout.amount, operation.timeout.unit
        )

        if (!operation.reactions.isEmpty()) {
            val iterator = operation.reactions.iterator()

            val chain = object : Consumer<Void> {
                override fun accept(t: Void) {
                    if (iterator.hasNext()) {
                        message.addReaction(iterator.next()).queue(this)
                    }
                }
            }

            message.clearReactions().queue(chain)
        }
    }

    fun stopOperation(messageId: String) {
        val operation = OPERATIONS.remove(messageId)

        if (operation != null) {
            operation.timeoutFuture?.cancel(true)
            operation.timeoutFuture = null

            operation.onRemoved()
        }
    }
}
