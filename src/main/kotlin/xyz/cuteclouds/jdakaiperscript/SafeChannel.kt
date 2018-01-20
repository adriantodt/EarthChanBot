package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.entities.TextChannel

class SafeChannel(channel: TextChannel) : SafeJDAObject<TextChannel>(channel) {
    val topic: String
        get() = obj.topic

    val isNSFW: Boolean
        get() = obj.isNSFW

    val name: String
        get() = obj.name

    val mention: String
        get() = obj.asMention
}
