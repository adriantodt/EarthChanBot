package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.entities.ISnowflake

import java.time.OffsetDateTime

open class SafeJDAObject<out T : ISnowflake>(val obj: T) {

    val id: String
        get() = obj.id

    val idLong: Long
        get() = obj.idLong

    val creationTime: OffsetDateTime
        get() = obj.creationTime

    override fun toString(): String {
        return obj.toString()
    }
}
