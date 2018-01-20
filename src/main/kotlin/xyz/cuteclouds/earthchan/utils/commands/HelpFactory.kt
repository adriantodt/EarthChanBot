package xyz.cuteclouds.earthchan.utils.commands

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.CommandPermission
import xyz.cuteclouds.earthchan.core.commands.helpEmbed
import xyz.cuteclouds.earthchan.data.config.ConfigManager
import java.awt.Color
import java.util.*

class HelpFactory(
    val name: String,
    private val permission: CommandPermission? = null
) {
    private var color: Color? = null
    private var description: String? = null
    private val usages = LinkedList<String>()

    private val tweaks = LinkedList<EmbedBuilder.() -> Unit>()

    fun build(event: GuildMessageReceivedEvent): MessageEmbed {
        return helpEmbed(event, name, permission).apply {
            if (color != null) {
                setColor(color)
            }

            if (description != null) {
                addField("Description:", description, false)
            }

            if (!usages.isEmpty()) {
                addField("Usages:", usages.joinToString("\n"), false)
            }

            tweaks.forEach { it(this) }
        }.build()
    }

    fun withEmbed(block: EmbedBuilder.() -> Unit): HelpFactory {
        tweaks.add(block)
        return this
    }

    fun color(color: Color): HelpFactory {
        this.color = color
        return this
    }

    fun description(description: String): HelpFactory {
        this.description = description
        return this
    }

    fun usage(command: String, description: String): HelpFactory {
        usages.add(command.usage(description))
        return this
    }

    companion object {
        val prefix = ConfigManager.config.prefixes.first()
    }
}

fun String.usage(description: String): String = "`${HelpFactory.prefix}$this` - $description"
