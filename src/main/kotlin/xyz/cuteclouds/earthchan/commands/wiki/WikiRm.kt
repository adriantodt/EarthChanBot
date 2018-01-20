package xyz.cuteclouds.earthchan.commands.wiki

import com.theorangehub.dml.DML
import com.theorangehub.dml.DMLBuilder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.commands.CommandPermission
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.baseEmbed
import xyz.cuteclouds.earthchan.data.entities.WikiArticle
import xyz.cuteclouds.earthchan.utils.commands.Emote
import xyz.cuteclouds.earthchan.utils.helpers.IntegerSelector
import java.util.concurrent.TimeUnit

//@Command("wikirm")
class WikiRm : ICommand, ICommand.Permission {

    override val permission = CommandPermission.BOT_OWNER

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        val artigos = Bot.db.getWikiByPageId(args)
        handleSelection(event, artigos, args)
    }

    private fun handle(event: GuildMessageReceivedEvent, article: WikiArticle) {
        article.delete()
        event.channel.sendMessage(Emote.SUCCESS + "Artigo `" + article.id + "` removido.").queue()
    }

    private fun handleSelection(event: GuildMessageReceivedEvent, articles: List<WikiArticle>, content: String) {
        if (articles.isEmpty()) {
            event.channel.sendMessage(
                Emote.ERROR + "Nenhum Artigo disponível."
            ).queue()
            return
        }

        if (articles.size == 1) {
            //event.getChannel().sendMessage(page(event, fichas.get(0))).queue();
            handle(event, articles[0])
            return
        }

        val builder = baseEmbed(event, "Selecione o Artigo:")

        var i = 0
        for ((_, _, page) in articles) {
            i++

            val embed = DML.parse(object : DMLBuilder() {
                override fun newEmbedBuilder(): EmbedBuilder {
                    return super.newEmbedBuilder()
                        .setColor(event.member.color)
                        .setFooter("Requerido por " + event.member.effectiveName, event.author.effectiveAvatarUrl)
                }
            }, page
            ).buildEmbed()

            val title = if (embed.author != null && embed.author.name != null)
                embed.author.name
            else if (embed.title != null) embed.title else "Sem Título"

            builder.addField("[$i] $title", searchHighlighting(content, pseudoDescription(embed)), true)

            if (i > 6) break
        }

        val message = event.channel.sendMessage(builder.build()).complete()

        val value = IntegerSelector(event)
            .min(1)
            .max(articles.size)
            .initialTimeout(20, TimeUnit.SECONDS)
            .build()

        val v = value.get() - 1

        message.delete().queue()

        handle(event, articles[v])
    }
}