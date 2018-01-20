package xyz.cuteclouds.earthchan.commands.luck

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.showHelp
import xyz.cuteclouds.earthchan.utils.commands.Emote.GAME_DIE
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory.Companion.prefix
import java.util.*
import kotlin.math.roundToLong

@Command("dice", "roll")
class Dice : ICommand, ICommand.HelpDialog {
    private val random: Random = Random()

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        if (args.isEmpty()) {
            event.channel.sendMessage(GAME_DIE + " You rolled a `" + (random.nextInt(20) + 1) + "` on a **D20**.").queue()
            return
        }

        if (args == "help") showHelp()

        try {
            event.channel.sendMessage(GAME_DIE + " You rolled `" + DiceEvaluator(args).parse().toPrettyString() + "`").queue()
        } catch (_: Exception) {
            showHelp()
        }
    }

    private fun Double.toPrettyString(): String = if (this % 1 == 0.0) roundToLong().toString() else this.toString()

    private val helpFactory = HelpFactory("Dice Command")
        .description("Have some fun, roll a dice.")
        .withEmbed {
            addField(
                "Examples:",
                arrayOf(
                    "d20",
                    "2d10",
                    "1d5 - 2",
                    "3d4 + 1d20",
                    "d360 * pi"
                ).joinToString(prefix = "```\n", separator = "\n", postfix = "\n```") { "${prefix}dice $it" },
                false
            )
        }

    override fun helpEmbed(event: GuildMessageReceivedEvent) = helpFactory.build(event)
}