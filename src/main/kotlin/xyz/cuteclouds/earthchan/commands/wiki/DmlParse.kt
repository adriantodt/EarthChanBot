package xyz.cuteclouds.earthchan.commands.wiki

import com.theorangehub.dml.DML
import com.theorangehub.dml.DMLBuilder
import com.theorangehub.dml.SyntaxException
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.echanColor
import xyz.cuteclouds.earthchan.core.commands.showHelp
import xyz.cuteclouds.earthchan.utils.commands.Emote.CONFUSED

@Command("dmlparse")
class DmlParse : ICommand, ICommand.HelpHandler {
    override fun onHelp(event: GuildMessageReceivedEvent) {
        event.channel.sendMessage("Ehm... This is something Adrian made so he could test the pages before going to the wiki.").queue()
    }

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        if (args.isEmpty()) showHelp()

        try {
            val message = DML.parse(
                object : DMLBuilder() {
                    override fun newEmbedBuilder(): EmbedBuilder {
                        return super.newEmbedBuilder()
                            .setColor(echanColor)
                            .setFooter("Required by " + event.member.effectiveName, event.author.effectiveAvatarUrl)
                    }
                },
                args
            )

            if ((message.rawGetMessage() == null || message.message.isEmpty) && (message.rawGetEmbed() == null || message.embed.isEmpty)) {
                event.channel.sendMessage(
                    "$CONFUSED Uh... Blank lines?"
                ).queue()
            }

            event.channel.sendMessage(
                message.build()
            ).queue()
        } catch (e: SyntaxException) {
            event.channel.sendMessage(
                "$CONFUSED Uh... Sorry, but I can't understand this. It doesn't look like **HTML** neither **DML**.\n${e.message}"
            ).queue()
            e.printStackTrace()
        }
    }
}
