@file:JvmName("WikiCommands")
package xyz.cuteclouds.earthchan.commands.wiki

import net.dv8tion.jda.core.entities.MessageEmbed
import org.apache.commons.lang3.StringUtils
import xyz.cuteclouds.earthchan.utils.DiscordUtils
import java.util.*

internal fun near(string: String, index: Int, size: Int, range: Int): String {
    val bot = index - range / 2
    val top = index + size + range / 2
    val nBot = Math.max(0, bot)
    val nTop = Math.min(string.length, top)
    val s = StringBuilder()

    if (bot == nBot) {
        s.append("...")
    }

    s.append(string.substring(nBot + if (bot == nBot) 3 else 0, nTop - if (top == nTop) 3 else 0))

    if (top == nTop) {
        s.append("...")
    }

    return s.toString()
}

internal fun pseudoDescription(embed: MessageEmbed): String {
    val joiner = StringJoiner("\n")

    if (embed.description != null) {
        joiner.add(embed.description)
    }

    for (field in embed.fields) {
        if (field.name != null) {
            joiner.add(field.name)
        }

        if (field.value != null) {
            joiner.add(field.value)
        }
    }

    if (embed.footer != null && embed.footer.text != null) {
        joiner.add(embed.footer.text)
    }

    return joiner.toString()
}

internal fun searchHighlighting(content: String?, description: String): String {
    val d = if (content == null) description else DiscordUtils.stripFormatting(description)
    val index = if (content == null) -1 else d.indexOf(content)

    return if (index != -1) {
        StringUtils.replace(near(d, index, content!!.length, 256), content, "**$content**")
    } else {
        if (d.length <= 256) {
            d
        } else {
            d.substring(253) + "..."
        }
    }
}