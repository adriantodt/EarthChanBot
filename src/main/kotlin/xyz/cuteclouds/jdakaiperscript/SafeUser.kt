package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.entities.User

open class SafeUser(user: User) : SafeJDAObject<User>(user) {

    open val name: String
        get() = obj.name

    val discriminator: String
        get() = obj.discriminator

    val avatar: String
        get() = obj.effectiveAvatarUrl

    val isBot: Boolean
        get() = obj.isBot

    val mention: String
        get() = obj.asMention
}
