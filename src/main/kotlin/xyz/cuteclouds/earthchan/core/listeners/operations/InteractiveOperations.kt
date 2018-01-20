package xyz.cuteclouds.earthchan.core.listeners.operations

import br.com.brjdevs.java.utils.async.threads.builder.ThreadBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.listeners.OptimizedListener
import xyz.cuteclouds.earthchan.extensions.classOf
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object InteractiveOperations : OptimizedListener<GuildMessageReceivedEvent>(classOf()) {
    private val EXECUTOR = Executors.newSingleThreadScheduledExecutor(ThreadBuilder().setName("InteractiveOperations Executor"))

    private val operations = ConcurrentHashMap<String, InteractiveOperation>()

    override fun event(event: GuildMessageReceivedEvent) {
        val id = event.channel.id
        val operation = operations[id]

        if (operation != null) {
            operation.timeoutFuture?.cancel(true)
            operation.timeoutFuture = null

            if (operation.onMessage(event)) {
                operations.remove(id, operation)
            } else {
                scheduleTimeout(operation, false)
            }
        }
    }

    fun builder(): InteractiveOperationBuilder {
        return InteractiveOperationBuilder()
    }

    fun register(operation: InteractiveOperation) {
        if (operations.containsKey(operation.channelId))
            throw IllegalStateException("Operation already happening at channelId")

        operations.put(operation.channelId, operation)

        scheduleTimeout(operation, true)
    }

    private fun scheduleTimeout(operation: InteractiveOperation, first: Boolean) {
        val timeAmount = (if (first) operation.initialTimeout else operation.increasingTimeout) ?: return

        operation.timeoutFuture = EXECUTOR.schedule(
            {
                operations.remove(operation.channelId, operation)

                operation.onTimeout()

            }, timeAmount.amount, timeAmount.unit
        )
    }

    fun stopOperation(channelId: String) {
        val operation = operations.remove(channelId)

        if (operation != null) {
            operation.timeoutFuture?.cancel(true)
            operation.timeoutFuture = null

            operation.onRemoved()
        }
    }
}
