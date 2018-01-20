package xyz.cuteclouds.earthchan.commands.info

import br.com.brjdevs.java.utils.collections.CollectionUtils.random
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.baseEmbed
import xyz.cuteclouds.earthchan.core.commands.onHelp
import xyz.cuteclouds.earthchan.utils.commands.Emote
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import javax.inject.Inject

@Command("help")
class Help
@Inject constructor(
    private val registry: CommandProcessorAndRegistry
) : ICommand, ICommand.HelpDialog {

    private val jokes = listOf(
        "Yo damn I heard you like help, because you just issued the help command to get the help about the help command.",
        "Congratulations, you managed to use the help command.",
        "Helps you to help yourself.",
        "Help Inception.",
        "A help helping helping helping help.",
        "I wonder if this is what you are looking for..."
    )

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        if (args.isEmpty()) {
            event.channel.sendMessage(
                baseEmbed(event, "Earth-chan Bot | Help")
                    .setDescription(
                        registry.commands.entries
                            .filter {
                                val (_, v) = it
                                !(v is ICommand.Invisible || v is ICommand.Permission && !v.permission.test(event.member))
                            }
                            .map { it.key }
                            .sorted()
                            .joinToString(prefix = "`", separator = "` `", postfix = "`")
                    ).build()
            ).queue()
        } else {
            val command = registry.commands[args]
            if (command != null) {
                onHelp(command, event)
            } else {
                event.channel.sendMessage("${Emote.ERROR} T-there's no command with that name!").queue()
            }
        }
    }

    override fun helpEmbed(event: GuildMessageReceivedEvent): MessageEmbed {
        return HelpFactory("Help Command")
            .description("**${random(jokes, Bot.random)}**")
            .usage("help", "Lists all commands.")
            .usage("help <command>", "Displays a command's help.")
            .build(event)
    }
}