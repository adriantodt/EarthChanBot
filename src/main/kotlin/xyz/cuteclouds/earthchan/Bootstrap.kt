@file:JvmName("Bootstrap")

package xyz.cuteclouds.earthchan

import br.com.brjdevs.java.utils.async.Async.future
import br.com.brjdevs.java.utils.collections.CollectionUtils.random
import mu.KotlinLogging.logger
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDAInfo
import net.dv8tion.jda.core.entities.Game.playing
import xyz.cuteclouds.earthchan.Bot.bootQuotes
import xyz.cuteclouds.earthchan.Bot.random
import xyz.cuteclouds.earthchan.Bot.splashes
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.core.init.BotInitializer
import xyz.cuteclouds.earthchan.core.injections.BotInjections
import xyz.cuteclouds.earthchan.core.listeners.command.CommandListener
import xyz.cuteclouds.earthchan.core.listeners.operations.InteractiveOperations
import xyz.cuteclouds.earthchan.core.listeners.operations.ReactionOperations
import xyz.cuteclouds.earthchan.extensions.client
import xyz.cuteclouds.earthchan.extensions.invoke
import xyz.cuteclouds.earthchan.extensions.newTask
import xyz.cuteclouds.earthchan.logging.DiscordLogBack
import xyz.cuteclouds.earthchan.logging.TerminalConsoleAdaptor
import java.util.*
import java.util.concurrent.TimeUnit

val log = logger {}

fun start() {
    log.info { "EarthChanBot starting..." }

    val initializer = future { BotInitializer("xyz.cuteclouds.earthchan") }

    val config = Bot.config

    val jda = client(AccountType.BOT) {
        setToken(config.tokens.discord)
        setAutoReconnect(true)
        setAudioEnabled(false)
        setCorePoolSize(5)
        setGame(playing(random(bootQuotes, random)))
    }

    DiscordLogBack.enable(jda.getTextChannelById(config.channels.logging))

    log.info { "[-=-=-=-=-=- EARTHCHANBOT STARTED -=-=-=-=-=-]" }
    log.info { "EarthChanBot v${Bot.VERSION} (JDA v${JDAInfo.VERSION}) started." }
    log.info { "Based on Unsharded MantaroBot and HbdvBot." }
    log.info { "[-=-=-=-=-=- -=-=-=-=-==-=-=-=-=- -=-=-=-=-=-]" }

    val injector = BotInjections(jda).toInjector()

    initializer().initialize(injector)

    jda.addEventListener(
        injector<CommandListener>(),
        InteractiveOperations,
        ReactionOperations
    )

    newTask("Splash Thread", 1, TimeUnit.MINUTES) {
        jda.presence.game = playing("${config.prefixes[0]}help | ${random(splashes, random)}")
    }

    log.info { "Finished! ${injector<CommandProcessorAndRegistry>().commands.size} commands loaded!" }

}

fun main(args: Array<String>) {
    Locale.setDefault(Locale("en", "US"))

    TerminalConsoleAdaptor.initializeTerminal()

    try {
        start()
    } catch (e: Exception) {
        DiscordLogBack.disable()
        log.error("Error during load!", e)
        log.error("Impossible to continue, aborting...")
        System.exit(-1)
    }

}
