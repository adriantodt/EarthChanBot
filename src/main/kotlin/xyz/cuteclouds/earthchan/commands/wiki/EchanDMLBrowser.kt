package xyz.cuteclouds.earthchan.commands.wiki

import com.theorangehub.dml.DML
import com.theorangehub.dml.DMLBuilder
import com.theorangehub.dml.DMLReaction
import com.theorangehub.dmlbrowser.BotBasedDMLBrowser
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.avarel.kaiper.runtime.java.JavaObject
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.commands.baseEmbed
import xyz.cuteclouds.earthchan.data.entities.WikiArticle
import xyz.cuteclouds.earthchan.utils.commands.Emote.ERROR
import xyz.cuteclouds.jdakaiperscript.SafeGuildMessageReceivedEvent
import xyz.cuteclouds.kaiperscript.parser.InterpreterEvaluator
import xyz.cuteclouds.kaiperscript.parser.KaiperScriptExecutor

class EchanDMLBrowser(event: GuildMessageReceivedEvent) : BotBasedDMLBrowser(event) {
    val emotes: List<String> = (1 until 6).map { "$it\u20e3" }
    private object SpecialHandling : RuntimeException()

    constructor(event: GuildMessageReceivedEvent, page: String) : this(event) {
        handle(page)
    }


    private fun navigateTo(pageId: String, index: Int = -1): String? {
        val pages = Bot.db.getWikiByPageId(pageId)

        return when {
            index != -1 -> pages.getOrNull(index)?.page
            pages.size == 1 -> pages.first().page
            else -> {
                handleSelection(pages)
                throw SpecialHandling
            }
        }
    }

    fun handleSelection(articles: List<WikiArticle>, content: String? = null) {
        if (articles.isEmpty()) {
            sendMessage(error("No Matches", "Sorry, I just don't found anything!"))
            return
        }

        if (articles.size == 1) {
            sendMessage(parse(articles.first().page))
            return
        }

        val dmlBuilder = DMLBuilder()
        val builder = baseEmbed(event, "Select the Page:")
        dmlBuilder.embed = builder

        var i = 0
        for ((_, pageId, page) in articles) {
            i++

            val embed = parse(page).buildEmbed()

            val title = if (embed.author != null && embed.author.name != null)
                embed.author.name
            else if (embed.title != null) embed.title else "No Title"

            builder.addField("[$i] $title", searchHighlighting(content, pseudoDescription(embed)), true)
            dmlBuilder.reactions.add(DMLReaction(emotes[0], "$pageId~$i", "$pageId~$i"))

            if (i > 6) break
        }

        sendMessage(dmlBuilder)
    }

    override fun parse(content: String): DMLBuilder {
        return DML.parse(newBuilder(), KaiperScriptExecutor(content)
            .execute(
                InterpreterEvaluator()
                    .declare("event", JavaObject(SafeGuildMessageReceivedEvent(event)))
            )
        )
    }

    override fun handle(page: String?) {
        try {
            super.handle(page)
        } catch (_: SpecialHandling) {
            //Trust that we handled stuff
        }
    }

    override fun navigate(pageId: String): String? {
        return if (pageId.contains('~')) {
            val (id, sIndex) = pageId.split('~', limit = 2)
            val index = sIndex.toIntOrNull()

            if (index == null) {
                navigateTo(pageId)
            } else {
                navigateTo(id, index)
            }
        } else {
            navigateTo(pageId)
        }
    }

    fun error(type: String, content: String): DMLBuilder {
        return newBuilder().apply {
            embed = baseEmbed(event, "Error | $type").apply {
                addField(
                    "Oops..",
                    "$ERROR $content",
                    false
                )
            }
        }
    }

    override fun notFound(pageId: String?): DMLBuilder = error("Not Found", "Sorry, I couldn't find the page ``$pageId``!")

}