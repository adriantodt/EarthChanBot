@file:Suppress("NOTHING_TO_INLINE")

package xyz.cuteclouds.earthchan.core.commands

import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.intellij.lang.annotations.MagicConstant
import xyz.cuteclouds.utils.StringUtils
import xyz.cuteclouds.utils.args.ArgParser
import xyz.cuteclouds.utils.args.ParserOptions
import xyz.cuteclouds.utils.args.tuples.Tuple

//Annotation
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(
    vararg val value: String
)

//Base
interface ICommand {
    fun call(event: GuildMessageReceivedEvent, args: String)

    interface Permission {
        val permission: CommandPermission
    }

    interface ExceptionHandler {
        fun handle(event: GuildMessageReceivedEvent, exception: Exception)
    }

    interface HelpDialog {
        fun helpEmbed(event: GuildMessageReceivedEvent) : MessageEmbed
    }

    interface HelpHandler {
        fun onHelp(event: GuildMessageReceivedEvent)
    }

    interface Invisible
}

//Common implementations
abstract class CommandWithArgs<T> : ICommand {
    override fun call(event: GuildMessageReceivedEvent, args: String) {
        call(event, args(event, args))
    }

    protected abstract fun call(event: GuildMessageReceivedEvent, args: T)

    protected abstract fun args(event: GuildMessageReceivedEvent, args: String): T
}

abstract class SimpleArgsCommand(
    private val expectedArgs: Int = 0,
    private val rest: Boolean = false
) : CommandWithArgs<Array<String>>() {
    override fun args(event: GuildMessageReceivedEvent, args: String): Array<String> = StringUtils.splitArgs(args, expectedArgs, rest)
}

abstract class AdvancedCommand : CommandWithArgs<Map<String, String>>() {
    override fun args(event: GuildMessageReceivedEvent, args: String): Map<String, String> = StringUtils.parse(event.message.contentRaw)
}

abstract class ArgsCommand(
    @MagicConstant(flagsFromClass = ParserOptions::class)
    private val options: Int = ParserOptions.DEFAULT
) : CommandWithArgs<Tuple>() {
    override fun args(event: GuildMessageReceivedEvent, args: String): Tuple = ArgParser(event.message.contentRaw, options).parse()
}