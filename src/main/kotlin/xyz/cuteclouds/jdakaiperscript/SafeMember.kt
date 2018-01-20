package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.Member
import java.awt.Color
import java.time.OffsetDateTime

class SafeMember(private val member: Member) : SafeUser(member.user) {

    val color: Color
        get() = member.color

    val joinDate: OffsetDateTime
        get() = member.joinDate

    val game: Game
        get() = member.game

    val onlineStatus: OnlineStatus
        get() = member.onlineStatus

    override val name: String
        get() = member.effectiveName

    val isOwner: Boolean
        get() = member.isOwner

    val roles: List<SafeRole>
        get() = member.roles.map(::SafeRole)

    override fun toString(): String {
        return member.toString()
    }
}
