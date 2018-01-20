package xyz.cuteclouds.earthchan.commands.eastereggs

import br.com.brjdevs.java.utils.collections.CollectionUtils.random
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.CommandPermission.BOT_OWNER
import xyz.cuteclouds.earthchan.core.commands.CommandPermission.SERVER_ADMIN
import xyz.cuteclouds.earthchan.core.commands.ICommand
import java.text.MessageFormat
import java.util.*

@Command("hi", "hai", "hello")
class Greeting : ICommand, ICommand.Invisible {

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        event.channel.sendMessage(
            random(event.member).format(arrayOf(event.member.effectiveName))
        ).queue()
    }

    companion object {

        private val speeches = listOf(
            "Oh, hai {0}, how's going?",
            "H-hi. I'm Earth-chan, what's your name!",
            "H-hai!",
            "Hello everyone!"
        )

        private val speechesAdmin = listOf(
            "Oh, {0}!! How are you?",
            "Hi {0}! Hope you're having a great day!"
        )

        private val speechesOwner = listOf(
            "Oh, {0}!! How are you?",
            "Oh, haii {0}! How are you, boi?",
            "Hi {0}! Hope you're having a wonderful day!"
        )

        private fun random(member: Member): MessageFormat {
            return get(
                random(
                    when {
                        BOT_OWNER.test(member) -> speechesOwner
                        SERVER_ADMIN.test(member) -> speechesAdmin
                        else -> speeches
                    }
                )
            )
        }

        private val cached = HashMap<String, MessageFormat>()

        private fun get(format: String) = cached.computeIfAbsent(format.replace("'", "''"), ::MessageFormat)
    }

}