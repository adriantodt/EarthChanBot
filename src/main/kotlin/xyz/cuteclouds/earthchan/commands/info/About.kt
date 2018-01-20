package xyz.cuteclouds.earthchan.commands.info

import br.com.brjdevs.java.utils.async.Async
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.baseEmbed
import xyz.cuteclouds.earthchan.core.commands.showHelp
import xyz.cuteclouds.earthchan.utils.FakeUserCache
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import java.lang.management.ManagementFactory
import java.util.*
import javax.inject.Inject

@Command("about")
class About
@Inject constructor(
    private val jda: JDA,
    private val userCache: FakeUserCache,
    private val registry: CommandProcessorAndRegistry
) : ICommand, ICommand.HelpDialog {
    init {
        userCache.asyncCache(155867458203287552, 293884638101897216, 388752909510115330, 281021121183219712)
    }

    override fun call(event: GuildMessageReceivedEvent, args: String) {
        when (args) {
            "credits" -> credits(event)
            "earth-chan", "earthchan", "earth", "echan" -> aboutEarthChan(event)
            "me", "bot", "" -> about(event)
            else -> showHelp()
        }
    }

    private fun aboutEarthChan(event: GuildMessageReceivedEvent) {

    }

    private fun discordTag(id: String): String {
        val user = userCache.retrieveUserById(id.toLong())
        return "**${user.name}#${user.discriminator}**"
    }

    private fun credits(event: GuildMessageReceivedEvent) {
        event.channel.sendMessage(
            baseEmbed(event, "Earth-chan Bot | Credits")
                .addField(
                    "Developers",
                    arrayOf(
                        "${discordTag("217747278071463937")}: Main Developer"
                    )
                )
                .addField(
                    "BotMDK Framework",
                    arrayOf(
                        "**Mantaro** by ${discordTag("155867458203287552")}", //Kode
                        "**Diax** by ${discordTag("293884638101897216")}", //Raine
                        "*(the bots' source code used to assemble the base framework)*"
                    )
                )
                .addField(
                    "EchanAnimu",
                    arrayOf(
                        "A project by ${discordTag("388752909510115330")}", //leegi
                        ""
                    )
                )
                .addField(
                    "Avatar \"Earth-chan by chinchongcha\"",
                    arrayOf(
                        "by **chinchongcha** ([DeviantArt](https://chinchongcha.deviantart.com/))",
                        "*([Click here to see the original Image](https://chinchongcha.deviantart.com/art/Earth-chan-724480877))*",
                        "\n**Image being used without authorization (but we're trying to contact them)**",
                        "\nSuggested by ${discordTag("281021121183219712")}"
                    )
                )
                .build()
        ).queue()
    }

    private fun about(event: GuildMessageReceivedEvent) {
        //MessageFirstTM
        val futureMessage = event.channel.sendMessage(
            baseEmbed(event, "Earth-chan Bot")
                .setDescription("Echan-Animu's official bot!")
                .addField("Info:", "<a:loading:403963064942329857> Gathering <a:loading:403963064942329857>", false)
                .setFooter("Commands executed: ${registry.commandCount}", event.jda.selfUser.avatarUrl)
                .build()
        ).submit()

        Async.sleep(2500)

        val guildCount = jda.guilds.size
        val usersCount = jda.users.size
        val onlineCount = jda.guilds
            .flatMap { it.members }
            .filter { it.onlineStatus != OnlineStatus.OFFLINE }
            .map { it.user.id }
            .distinct()
            .count()
        val tcCount = jda.textChannels.size
        val vcCount = jda.voiceChannels.size
        var seconds = ManagementFactory.getRuntimeMXBean().uptime / 1000
        var minutes = seconds / 60
        var hours = minutes / 60
        val days = hours / 24

        seconds %= 60
        minutes %= 60
        hours %= 24

        val uptime = Formatter().format("%d:%02d:%02d:%02d", days, hours, minutes, seconds).toString()

        val info = arrayOf(
            "**Bot Version**: ${Bot.VERSION}",
            "**Uptime**: $uptime",
            "**Threads**: ${Thread.activeCount()}",
            "**Servers**: $guildCount",
            "**Users (Online/Total)**: $onlineCount/$usersCount",
            "**Text/Voice Channels**: $tcCount/$vcCount"
        ).joinToString("\n")

        with(futureMessage.get()) {
            editMessage(
                EmbedBuilder(embeds[0])
                    .clearFields()
                    .addField("Info:", info, false)
                    .build()
            ).queue()
        }
    }

    private val helpFactory = HelpFactory("About")
        .description("Shows my status and info. No, I'm not sick.")

    override fun helpEmbed(event: GuildMessageReceivedEvent) = helpFactory.build(event)
}

fun EmbedBuilder.addField(name: String, value: Array<String>, inline: Boolean = false) = addField(name, value.joinToString("\n"), inline)
