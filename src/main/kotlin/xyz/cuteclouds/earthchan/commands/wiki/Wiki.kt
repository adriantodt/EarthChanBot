package xyz.cuteclouds.earthchan.commands.wiki

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.commands.SimpleArgsCommand
import xyz.cuteclouds.earthchan.core.commands.showHelp
import xyz.cuteclouds.earthchan.data.entities.WikiArticle
import xyz.cuteclouds.earthchan.utils.DiscordUtils

//@Command("wiki")
class Wiki : SimpleArgsCommand(expectedArgs = 1, rest = true) {
    override fun call(event: GuildMessageReceivedEvent, args: Array<String>) {
        when (args.firstOrNull()) {
            null, "home", "index" -> {
                get(event, "index")
            }
            "page", "get" -> {
                get(event, args.getOrNull(1) ?: return showHelp())
                return
            }
            "search" -> {
                search(event, args.getOrNull(1) ?: return showHelp())
                return
            }
            "last", "latest" -> {
                latest(event)
                return
            }
        }

        showHelp()
    }

    private operator fun get(event: GuildMessageReceivedEvent, page: String) {
        EchanDMLBrowser(event, page)
    }

    private fun latest(event: GuildMessageReceivedEvent) {
        EchanDMLBrowser(event).handleSelection(
            Bot.db.getLastWiki()
                .sortedByDescending(WikiArticle::id)
        )
    }

    private fun search(event: GuildMessageReceivedEvent, p: String) {
        val page = DiscordUtils.stripFormatting(p)

        EchanDMLBrowser(event).handleSelection(
            Bot.db.searchWiki(page)
                .sortedByDescending(WikiArticle::id),
            page
        )
    }
}