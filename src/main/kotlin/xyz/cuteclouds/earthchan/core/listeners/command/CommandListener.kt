package xyz.cuteclouds.earthchan.core.listeners.command

import br.com.brjdevs.java.utils.async.Async.thread
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.core.listeners.OptimizedListener
import xyz.cuteclouds.earthchan.extensions.classOf
import javax.inject.Inject

class CommandListener
@Inject
constructor(
    private val processor: CommandProcessorAndRegistry
) : OptimizedListener<GuildMessageReceivedEvent>(classOf()) {
    override fun event(event: GuildMessageReceivedEvent) {
        // @formatter:off
		if (
            event.author.isBot
                ||
            !event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_WRITE)
                &&
            !event.guild.selfMember.hasPermission(Permission.ADMINISTRATOR)
        ) return
        // @formatter:on

        thread("Cmd:${event.author.name}#${event.author.discriminator}:${event.message.contentRaw}") {
            processor.onCommand(event)
        }
    }

}
