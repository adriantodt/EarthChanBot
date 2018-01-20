package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.Region
import net.dv8tion.jda.core.entities.Guild

class SafeGuild(guild: Guild, private val channel: SafeChannel) : SafeJDAObject<Guild>(guild) {

    val name: String
        get() = obj.name

    val explicitContentLevel: Guild.ExplicitContentLevel
        get() = obj.explicitContentLevel

    val region: Region
        get() = obj.region

    val textChannels: List<SafeChannel>
        get() = obj.textChannels.map { c -> if (c.idLong == channel.idLong) channel else SafeChannel(c) }

    val roles: List<SafeRole>
        get() = obj.roles.map(::SafeRole)

    val members: List<SafeMember>
        get() = obj.members.map(::SafeMember)

    val owner: SafeMember
        get() = SafeMember(obj.owner)

    val iconUrl: String
        get() = obj.iconUrl

    fun getMembersByName(name: String, ignoreCase: Boolean): List<SafeMember> {
        return obj.getMembersByName(name, ignoreCase).map(::SafeMember)
    }

    fun getMembersByNickname(name: String, ignoreCase: Boolean): List<SafeMember> {
        return obj.getMembersByNickname(name, ignoreCase).map(::SafeMember)
    }

    fun getMembersByEffectiveName(name: String, ignoreCase: Boolean): List<SafeMember> {
        return obj.getMembersByEffectiveName(name, ignoreCase).map(::SafeMember)
    }
}
