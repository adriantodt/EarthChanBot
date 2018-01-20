package xyz.cuteclouds.earthchan.commands.info

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.utils.commands.Emote.MEGA
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import java.lang.System.currentTimeMillis

@Command("ping")
class Ping : ICommand, ICommand.HelpDialog {
    override fun call(event: GuildMessageReceivedEvent, args: String) {
        val start = currentTimeMillis()
        event.channel.sendTyping().queue {
            val ping = currentTimeMillis() - start
            event.channel.sendMessage("$MEGA **Ping**: `${ping}ms/${event.jda.ping}ms` (Discord API/Websocket)").queue()
        }
    }

    private val helpFactory = HelpFactory("Ping Command")
        .description("**Plays Ping-Pong with Discord and finds out how much it takes**.")

    override fun helpEmbed(event: GuildMessageReceivedEvent) = helpFactory.build(event)
}