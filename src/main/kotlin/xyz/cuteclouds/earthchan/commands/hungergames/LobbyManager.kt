package xyz.cuteclouds.earthchan.commands.hungergames

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

object LobbyManager {
    val executor by lazy { Executors.newSingleThreadScheduledExecutor()!! }
    val lobbys = LinkedHashMap<String, Lobby>()
    val futures = LinkedHashMap<String, ScheduledFuture<*>>()

    private fun schedule(channel: TextChannel, lobby: Lobby) {
        val id = channel.id
        futures.remove(id)?.cancel(false)
        futures[id] = executor.schedule(
            {
                lobbys.remove(id)
                futures.remove(id)
                //TODO message
            },
            2, TimeUnit.MINUTES
        )
    }

    fun getOrCreateLobby(channel: TextChannel, member: Member): Lobby {
        val id = channel.id
        val lobby = lobbys.computeIfAbsent(id) { Lobby(member) }
            schedule(channel, lobby)

        return lobby
    }

    fun lobbyExists(channel: TextChannel) = lobbys.contains(channel.id)

    fun registerLobby(channel: TextChannel, lobby: Lobby) {
        val id = channel.id
        lobbys[id] = lobby
        schedule(channel, lobby)
    }

    fun getLobby(channel: TextChannel): Lobby? {
        val id = channel.id
        val lobby = lobbys[id]
        if (lobby != null) {
            schedule(channel, lobby)
        }

        return lobby
    }

    fun removeLobby(channel: TextChannel): Lobby? {
        val id = channel.id
        val lobby = lobbys.remove(id)
        if (lobby != null) {
            futures.remove(id)?.cancel(false)
        }

        return lobby
    }
}