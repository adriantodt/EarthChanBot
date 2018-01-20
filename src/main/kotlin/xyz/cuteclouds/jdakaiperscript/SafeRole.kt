package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Role

class SafeRole(role: Role) : SafeJDAObject<Role>(role) {
    val name: String
        get() = obj.name

    val permissions: List<Permission>
        get() = obj.permissions

    val permissionsRaw: Long
        get() = obj.permissionsRaw

    val position: Int
        get() = obj.position

    val positionRaw: Int
        get() = obj.positionRaw

    val isManaged: Boolean
        get() = obj.isManaged

    val isPublicRole: Boolean
        get() = obj.isPublicRole

    val isMentionable: Boolean
        get() = obj.isMentionable

    val isSeparate: Boolean
        get() = obj.isHoisted
}
