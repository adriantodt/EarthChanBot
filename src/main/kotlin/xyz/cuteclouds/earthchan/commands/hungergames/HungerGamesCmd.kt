package xyz.cuteclouds.earthchan.commands.hungergames

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.commands.info.addField
import xyz.cuteclouds.earthchan.core.commands.*
import xyz.cuteclouds.earthchan.utils.commands.Emote.CONFUSED
import xyz.cuteclouds.earthchan.utils.commands.Emote.ERROR
import xyz.cuteclouds.earthchan.utils.commands.Emote.SUCCESS
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory.Companion.prefix
import xyz.cuteclouds.earthchan.utils.commands.usage
import xyz.cuteclouds.utils.args.ArgParser

@Command("hg", "hungergames")
class HungerGamesCmd : SimpleArgsCommand(expectedArgs = 1, rest = true), ICommand.HelpHandler, ICommand.Invisible {

    override fun call(event: GuildMessageReceivedEvent, args: Array<String>) {
        if (GameManager.isGameRunning(event.channel)) {
            when (args.firstOrNull()) {
                "cancel", "end", "finish" -> {
                    val game = GameManager.getGame(event.channel)

                    if (game == null) {
                        event.channel.sendMessage("$CONFUSED Uhhh... What?").queue()
                        return
                    }

                    if (event.author.id != game.lobby.adminId) {
                        event.channel.sendMessage("$ERROR You're not the admin of this game, silly!").queue()
                        return
                    }

                    game.thread.interrupt()
                    event.channel.sendMessage("$SUCCESS Game stopped.").queue()

                }
                else -> showHelp()
            }
        } else {
            when (args.firstOrNull()?.toLowerCase()) {
                "new" -> {
                    val lobby = LobbyManager.getOrCreateLobby(event.channel, event.member)
                    val created = lobby.adminId == event.author.id

                    event.channel.sendMessage(
                        if (created) "$SUCCESS **${event.member.effectiveName}** created a new lobby!\n" +
                            "Other players can run ``${prefix}hg join`` to join it!\n" +
                            "Use  ``${prefix}hg start`` to start the game!"
                        else
                            "$ERROR S-sorry, but a lobby (created by **${event.guild.getMemberById(lobby.adminId).effectiveName}**) already exists!\n" +
                                "Use ``${prefix}hg join`` to join it!"
                    ).queue()
                }
                "join" -> {
                    val lobby = LobbyManager.getLobby(event.channel)
                    if (lobby == null) {
                        event.channel.sendMessage(
                            "$ERROR S-sorry, but there's no lobby here!\n" +
                                "Use ``${prefix}hg new`` and create your lobby!"
                        ).queue()
                    } else if (lobby.adminId == event.author.id || lobby.players.contains(event.member)) {
                        event.channel.sendMessage(
                            "$ERROR You're already in that lobby, silly!"
                        ).queue()
                    } else {
                        lobby.players.add(event.member)
                        event.channel.sendMessage(
                            "$SUCCESS **${event.member.effectiveName}** joined **${event.guild.getMemberById(lobby.adminId).effectiveName}**'s lobby!"
                        ).queue()
                    }
                }
                "leave" -> {
                    val lobby = LobbyManager.getLobby(event.channel)

                    if (lobby == null) {
                        event.channel.sendMessage("$ERROR There's no lobby, silly!").queue()
                    } else if (lobby.adminId == event.author.id) {
                        LobbyManager.removeLobby(event.channel)

                        event.channel.sendMessage("$SUCCESS **${event.member.effectiveName}** closed their lobby.").queue()
                    } else if (!lobby.players.contains(event.member)) {
                        event.channel.sendMessage("$ERROR You're not in that lobby, silly!").queue()
                    } else {
                        lobby.players.remove(event.member)
                        event.channel.sendMessage(
                            "$SUCCESS **${event.member.effectiveName}** left **${event.guild.getMemberById(lobby.adminId).effectiveName}**'s lobby!"
                        ).queue()
                    }
                }
                "addguests" -> {
                    val lobby = LobbyManager.getLobby(event.channel)

                    if (lobby == null) {
                        event.channel.sendMessage("$ERROR There's no lobby for me to start a game, silly!").queue()
                        return
                    }

                    if (event.author.id != lobby.adminId) {
                        event.channel.sendMessage("$ERROR You're not the admin of this lobby, silly!").queue()
                        return
                    }

                    val arg = args.getOrNull(1)

                    if (arg == null) {
                        showHelp()
                        return
                    }

                    val list = ArgParser(arg).parse().mapNotNull { if (it.isText) it.asText() else null }

                    lobby.guests.addAll(list)

                    event.channel.sendMessage("$SUCCESS Added ${list.map { "**$it**" }.toSmartString()} as guests!").queue()
                }
            //"configs" -> { }
                "start" -> {
                    val lobby = LobbyManager.getLobby(event.channel)

                    if (lobby == null) {
                        event.channel.sendMessage("$ERROR There's no lobby for me to start a game, silly!").queue()
                        return
                    }

                    if (event.author.id != lobby.adminId) {
                        event.channel.sendMessage("$ERROR You're not the admin of this lobby, silly!").queue()
                        return
                    }

                    GameManager.newGame(event.channel, lobby)
                }
                null, "", "lobby" -> {
                    val lobby = LobbyManager.getLobby(event.channel)

                    if (lobby == null) {
                        event.channel.sendMessage(
                            "$ERROR S-sorry, but there's no lobby here!\n" +
                                "Use ``${prefix}hg new`` and create your lobby!"
                        ).queue()
                        return
                    }

                    event.channel.sendMessage(
                        baseEmbed(event, "HungerGames | ${event.guild.getMemberById(lobby.adminId).effectiveName}'s Lobby")
                            .addField(
                                "Players:",
                                if (lobby.players.isEmpty()) arrayOf("None") else lobby.players.map { "**${it.effectiveName}**" }.toTypedArray()
                            )
                            .addField(
                                "Guests:",
                                if (lobby.guests.isEmpty()) arrayOf("None") else lobby.guests.map { "**$it**" }.toTypedArray()
                            )
                            .build()
                    ).queue()
                }
                else -> showHelp()
            }
        }
    }

    override fun onHelp(event: GuildMessageReceivedEvent) {
        if (GameManager.isGameRunning(event.channel)) {
            event.channel.sendMessage(inGameHelp).queue()
        } else {
            event.channel.sendMessage(lobbyHelp.build(event)).queue()
        }
    }

    private val inGameHelp = arrayOf(
        "**HungerGames** - **In-game**",
        "hg next".usage("Shows next event."),
        "hg <end/finish>".usage("Ends the game."),
        "hg cancel".usage("Abruptly ends the game.")
    ).joinToString("\n")

    private val lobbyHelp = HelpFactory("HungerGames")
        .usage("hg new", "Creates a new game lobby.")
        .usage("hg join", "Joins a existing lobby.")
        .usage("hg addguests", "Adds guests to the lobby. (You must be the lobby's creator)")
        .usage("hg configs", "Setup the lobby configs. (You must be the lobby's creator)")
        .usage("hg start", "Starts a new game. (You must be the lobby's creator)")
}

private fun <E> List<E>.toSmartString(): String {
    if (isEmpty()) return "nothing"
    if (size == 1) return first().toString()
    if (size == 2) {
        val (e1, e2) = this
        return "$e1 and $e2"
    }
    val copy = toMutableList()
    val last = copy.removeAt(copy.size - 1)
    return copy.joinToString(", ", transform = Any?::toString, postfix = " and $last")
}

