package xyz.cuteclouds.earthchan.core.commands

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.utils.BotUtils.capitalize

enum class CommandPermission {
    USER {
        override fun test(member: Member)= true
    },
    SERVER_ADMIN {
        override fun test(member: Member) = member.isOwner
            || member.hasPermission(Permission.MANAGE_SERVER)
            || member.hasPermission(Permission.ADMINISTRATOR)
            || BOT_OWNER.test(member)
    },
    BOT_OWNER {
        override fun test(member: Member) = Bot.owners.contains(member.user.id)
    };

    abstract fun test(member: Member): Boolean

    override fun toString(): String {
        return capitalize(name.toLowerCase())
    }
}
