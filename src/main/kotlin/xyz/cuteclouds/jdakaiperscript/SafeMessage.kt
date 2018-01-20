package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.entities.Message

class SafeMessage(message: Message) : SafeJDAObject<Message>(message) {

    val display: String
        get() = obj.contentDisplay

    val raw: String
        get() = obj.contentRaw

    val stripped: String
        get() = obj.contentStripped
}
