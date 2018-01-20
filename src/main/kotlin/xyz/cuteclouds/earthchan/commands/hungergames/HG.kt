package xyz.cuteclouds.earthchan.commands.hungergames

import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.TextChannel
import xyz.cuteclouds.hunger.HungerGames
import xyz.cuteclouds.hunger.HungerGamesBuilder
import xyz.cuteclouds.hunger.data.SimpleTribute
import xyz.cuteclouds.hunger.events.EventFormatter
import xyz.cuteclouds.hunger.game.*
import xyz.cuteclouds.hunger.loader.loadFile
import xyz.cuteclouds.hunger.loader.parseHarmfulActions
import xyz.cuteclouds.hunger.loader.parseHarmlessActions
import xyz.cuteclouds.hunger.phases.*
import java.io.File

object HG {
    val actions: Actions by lazy {
        Actions(
            bloodbathHarmless = harmlessActions("assets/hungergames/events/bloodbath_harmless.txt"),
            bloodbathHarmful = harmfulActions("assets/hungergames/events/bloodbath_harmful.txt"),
            dayHarmless = harmlessActions("assets/hungergames/events/day_harmless.txt"),
            dayHarmful = harmfulActions("assets/hungergames/events/day_harmful.txt"),
            nightHarmless = harmlessActions("assets/hungergames/events/night_harmless.txt"),
            nightHarmful = harmfulActions("assets/hungergames/events/night_harmful.txt"),
            feastHarmless = harmlessActions("assets/hungergames/events/feast_harmless.txt"),
            feastHarmful = harmfulActions("assets/hungergames/events/feast_harmful.txt")
        )
    }

    fun buildHg(lobby: Lobby): HungerGames {
        return HungerGamesBuilder()
            .actions(actions)
            .addTributes(
                listOf(
                    lobby.players.map(::DiscordTribute),
                    lobby.guests.map(::SimpleTribute)
                ).flatten()
            )
            .threshold(lobby.threshold)
            .build()
    }

    private fun harmlessActions(file: String): List<HarmlessAction> = parseHarmlessActions(loadFile(File(file)))
    private fun harmfulActions(file: String): List<HarmfulAction> = parseHarmfulActions(loadFile(File(file)))


    private val formatter: EventFormatter = EventFormatter {
        "**${it.name}** ``(${if (it.kills == 1) "1 kill" else it.kills.toString() + " kills"})``"
    }

    fun handleHg(hungerGames: HungerGames, channel: TextChannel) {
        fun send(vararg messages: Any?) {
            channel.sendMessage(messages.joinToString("\n", transform = Any?::toString)).queue()
        }

        fun quickYield() = Thread.sleep(2500)
        fun yield() = Thread.sleep(15000)

        for (e: Phase in hungerGames.newGame()) {
            when (e) {
                is Bloodbath -> {
                    send("=-=- **The Bloodbath** -=-=")
                    quickYield()

                    for (blocks in e.events.withIndex().groupBy { it.index / 5 }.values) {
                        send(blocks.map(IndexedValue<Event>::value).joinToString("\n") { it.format(formatter) })
                        yield()
                    }
                }
                is Day -> {
                    send("=-=- **Day ${e.number}** -=-=")
                    quickYield()

                    for (blocks in e.events.withIndex().groupBy { it.index / 5 }.values) {
                        send(blocks.map(IndexedValue<Event>::value).joinToString("\n") { it.format(formatter) })
                        yield()
                    }
                }
                is FallenTributes -> {
                    val fallenTributes = e.fallenTributes

                    send(
                        "=-=- Fallen Tributes -=-=",
                        "${fallenTributes.size} cannon shots can be heard in the distance.",
                        fallenTributes.joinToString("\n") { "X ${it.format(formatter)}" }
                    )
                    yield()
                }
                is Night -> {
                    send("=-=- **Night ${e.number}** -=-=")
                    quickYield()

                    for (blocks in e.events.withIndex().groupBy { it.index / 5 }.values) {
                        send(blocks.map(IndexedValue<Event>::value).joinToString("\n") { it.format(formatter) })
                        yield()
                    }
                }
                is Feast -> {
                    send("=-=- **Feast (Day ${e.number})** -=-=")
                    quickYield()

                    for (blocks in e.events.withIndex().groupBy { it.index / 5 }.values) {
                        send(blocks.map(IndexedValue<Event>::value).joinToString("\n") { it.format(formatter) })
                        yield()
                    }
                }
                is Winner -> {
                    send(
                        "=-=- **Winner!** -=-=",
                        formatter.format("{0} is the winner!", listOf(e.winner))
                    )
                    return
                }
                is Draw -> {
                    send(
                        "=-=- **Draw!** -=-=",
                        "Everyone is dead. No winners."
                    )
                    return
                }
            }
        }
    }

}

class DiscordTribute(val member: Member) : Tribute() {
    override val name: String
        get() = member.effectiveName

    override fun copy() = DiscordTribute(member)
}
