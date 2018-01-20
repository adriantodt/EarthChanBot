package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed.Field

import java.awt.Color

class SafeEmbed {

    private val builder = EmbedBuilder()

    fun addBlankField(inline: Boolean): SafeEmbed {
        builder.addBlankField(inline)
        return this
    }

    fun addField(field: Field): SafeEmbed {
        builder.addField(field)
        return this
    }

    fun addField(name: String, value: String, inline: Boolean): SafeEmbed {
        builder.addField(name, value, inline)
        return this
    }

    fun appendDescription(description: CharSequence): SafeEmbed {
        builder.appendDescription(description)
        return this
    }

    @JvmOverloads
    fun author(name: String, url: String? = null, iconUrl: String? = null): SafeEmbed {
        builder.setAuthor(name, url, iconUrl)
        return this
    }

    fun clear(): SafeEmbed {
        builder.clearFields()
        return this
    }

    fun color(color: Color): SafeEmbed {
        builder.setColor(color)
        return this
    }

    fun description(description: CharSequence): SafeEmbed {
        builder.setDescription(description)
        return this
    }

    @JvmOverloads
    fun footer(text: String, iconUrl: String? = null): SafeEmbed {
        builder.setFooter(text, iconUrl)
        return this
    }

    fun image(url: String): SafeEmbed {
        builder.setImage(url)
        return this
    }

    fun thumbnail(url: String): SafeEmbed {
        builder.setThumbnail(url)
        return this
    }

    @JvmOverloads
    fun title(title: String, url: String? = null): SafeEmbed {
        builder.setTitle(title, url)
        return this
    }

    companion object {
        fun builder(e: SafeEmbed): EmbedBuilder {
            return e.builder
        }
    }
}
