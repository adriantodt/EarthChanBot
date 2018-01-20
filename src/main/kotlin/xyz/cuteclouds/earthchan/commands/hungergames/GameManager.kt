package xyz.cuteclouds.earthchan.commands.hungergames

import br.com.brjdevs.java.utils.async.Async
import net.dv8tion.jda.core.entities.TextChannel
import xyz.cuteclouds.earthchan.commands.hungergames.HG.buildHg
import xyz.cuteclouds.earthchan.commands.hungergames.HG.handleHg
import java.util.*

object GameManager {
    val games = LinkedHashMap<String, Game>()

    fun isGameRunning(channel: TextChannel) = games.contains(channel.id)

    fun registerGame(channel: TextChannel, game: Game) {
        games[channel.id] = game
    }

    fun newGame(channel: TextChannel, lobby: Lobby) {
        games[channel.id] = Game(
            lobby,
            Async.thread {
                try {
                    handleHg(buildHg(lobby), channel)
                } catch (_: InterruptedException) {
                } finally {
                    removeGame(channel)
                    LobbyManager.registerLobby(channel, lobby)
                }
            }
        )
    }

    fun getGame(channel: TextChannel): Game? {
        return games[channel.id]
    }

    fun removeGame(channel: TextChannel): Game? {
        return games.remove(channel.id)
    }
}