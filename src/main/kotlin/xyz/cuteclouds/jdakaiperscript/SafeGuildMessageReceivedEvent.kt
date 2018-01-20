package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class SafeGuildMessageReceivedEvent(private val event: GuildMessageReceivedEvent) {
    val channel: SafeChannel = SafeChannel(event.channel)
    val author: SafeMember = SafeMember(event.member)
    val me: SafeMember = SafeMember(event.guild.selfMember)
    val guild: SafeGuild = SafeGuild(event.guild, channel)
    val mentions: SafeMentions = SafeMentions(event.message.mentionedMembers)
    val message: SafeMessage = SafeMessage(event.message)

    override fun toString(): String = event.toString()
}
