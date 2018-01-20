package xyz.cuteclouds.earthchan.commands.wiki

import com.theorangehub.dml.DML
import com.theorangehub.dml.DMLBuilder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.*
import xyz.cuteclouds.earthchan.data.entities.WikiArticle
import xyz.cuteclouds.earthchan.utils.commands.Emote

//@Command("wikiadd")
class WikiAdd : SimpleArgsCommand(expectedArgs = 1, rest = true), ICommand.Permission {

    override val permission = CommandPermission.BOT_OWNER

    override fun call(event: GuildMessageReceivedEvent, args: Array<String>) {
        if (args.size < 2) {
            showHelp()
        }

        val article = WikiArticle(pageId = args[0], page = args[1])

        article.save()

        event.channel.sendMessage(
            DML.parse(object : DMLBuilder() {
                override fun newMessageBuilder(): MessageBuilder {
                    return super.newMessageBuilder()
                        .append(Emote.SUCCESS).append("**Artigo publicado!**\n\n")
                        .append("**ID**: `").append(article.id).append("`\n")
                        .append("**Preview**:")
                }

                override fun newEmbedBuilder(): EmbedBuilder {
                    return baseEmbed(event, "#HBDVRPG Wiki")
                }
            }, article.page
            ).build()
        ).queue()
    }
}