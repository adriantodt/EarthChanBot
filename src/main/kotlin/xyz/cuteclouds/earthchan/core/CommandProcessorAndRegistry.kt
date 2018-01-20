package xyz.cuteclouds.earthchan.core

import br.com.brjdevs.java.utils.texts.StringUtils
import com.rethinkdb.gen.exc.ReqlError
import com.theorangehub.dml.SyntaxException
import mu.KLogging
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.CommandExceptions
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.core.commands.onHelp
import xyz.cuteclouds.earthchan.data.config.Config
import xyz.cuteclouds.earthchan.utils.Snow64
import xyz.cuteclouds.earthchan.utils.commands.Emote.ERROR
import xyz.cuteclouds.earthchan.utils.commands.Emote.STOP
import xyz.cuteclouds.earthchan.utils.helpers.CommandStatsManager
import java.util.*
import javax.inject.Inject

class CommandProcessorAndRegistry
@Inject constructor(
    private val config: Config
) : CommandRegistry {

    companion object : KLogging()

    override val commands = HashMap<String, ICommand>()

    var commandCount = 0
        private set

    override fun register(s: String, c: ICommand) {
        commands.putIfAbsent(s, c)
    }

    fun onCommand(event: GuildMessageReceivedEvent) {
        val raw = event.message.contentRaw

        for (prefix in config.prefixes) {
            if (raw.startsWith(prefix)) {
                process(event, raw.substring(prefix.length))
                return
            }
        }

        if (raw.startsWith("<@")) {
            for (mention in listOf("<@${event.jda.selfUser.id}> ", "<@!${event.jda.selfUser.id}> ")) {
                if (raw.startsWith(mention)) {
                    process(event, raw.substring(mention.length))
                    return
                }
            }
        }

        //val guildPrefix = db[event.guild].settings.prefix
        //if (guildPrefix != null && raw.startsWith(guildPrefix)) {
        //    process(event, raw.substring(guildPrefix.length))
        //    return
        //}
    }

    private fun process(event: GuildMessageReceivedEvent, content: String) {
        val split = StringUtils.splitArgs(content, 2)
        val cmd = split[0]!!.toLowerCase()
        val args = split[1]!!

        val command = commands[cmd] ?: return ?: return processCustomCommand(event, cmd, args)

        if (command is ICommand.Permission && !command.permission.test(event.member)) {
            event.channel.sendMessage("$STOP B-baka, I'm not allowed to let you do that!").queue()
            return
        }

        runCommand(command, event, args)

        CommandStatsManager.log(cmd)

        logger.trace {
            "Command invoked: $cmd, by ${event.author.name}#${event.author.discriminator} with timestamp ${Date()}"
        }
    }

    private fun processCustomCommand(event: GuildMessageReceivedEvent, cmd: String, args: String) {
        // TODO: Implement?
    }

    private fun runCommand(command: ICommand, event: GuildMessageReceivedEvent, args: String) {
        try {
            command.call(event, args)
        } catch (e: Exception) {
            try {
                handleException(command, event, e)
            } catch (_: Exception) {
            }
        }

    }

    private fun handleException(c: ICommand, event: GuildMessageReceivedEvent, e: Exception) {
        if (e == CommandExceptions.Finish) {
            return
        }

        if (e == CommandExceptions.ShowHelp) {
            return onHelp(c, event)
        }

        if (c is ICommand.ExceptionHandler) {
            return c.handle(event, e)
        }

        val id = Snow64.toSnow64(event.message.idLong)

        logger.error(e) {
            "**ERROR REPORTED**\nErrorID: ``$id``\nType: ``${e.javaClass.simpleName}``\nCommand: ``${event.message.contentRaw}``\n\n"
        }

        when (e) {
            is SyntaxException -> {
                event.channel.sendMessage(
                    "$ERROR S-sorry, the page caught fire. Please, forgive me!\n(ErrorID: ``$id``)"
                ).queue()
            }
            is ReqlError -> {
                event.channel.sendMessage(
                    "$ERROR W-wha? I think I lost my notebook. W-we're still friends, right?\n(ErrorID: ``$id``)"
                ).queue()
            }
            else -> {
                event.channel.sendMessage(
                    "$ERROR What is happening? I'm sorry, I'm sorry, I'm sorry!\n(Error ID: ``$id``)"
                ).queue()
            }
        }
    }
}