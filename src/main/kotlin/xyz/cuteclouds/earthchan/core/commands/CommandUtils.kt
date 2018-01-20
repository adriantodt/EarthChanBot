package xyz.cuteclouds.earthchan.core.commands

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.utils.commands.Emote.ERROR
import xyz.cuteclouds.earthchan.utils.commands.Emote.STOP
import java.awt.Color
import java.util.concurrent.TimeUnit

object CommandExceptions {
    object ShowHelp : RuntimeException()
    object Finish : RuntimeException()
}

fun showHelp(): Unit = throw CommandExceptions.ShowHelp
fun finish(): Unit = throw CommandExceptions.Finish

val echanColor = Color(0x56CFEA)

@JvmOverloads
fun baseEmbed(event: GuildMessageReceivedEvent, name: String, image: String = event.jda.selfUser.effectiveAvatarUrl): EmbedBuilder {
    return EmbedBuilder()
        .setAuthor(name, null, image)
        .setColor(event.member.color ?: echanColor)
        .setFooter("Required by ${event.member.effectiveName}", event.author.effectiveAvatarUrl)
}

fun helpEmbed(event: GuildMessageReceivedEvent, name: String, permission: CommandPermission?): EmbedBuilder {
    return baseEmbed(event, name).apply {
        setThumbnail("https://my-dedicated.is-probably-not.online/help.png") //TODO CHANGE LOL
        if (permission != null) addField("Permission Required:", permission.toString(), false)
    }
}

fun onHelp(command: ICommand, event: GuildMessageReceivedEvent) {
    if (command is ICommand.Permission && !command.permission.test(event.member)) {
        event.channel
            .sendMessage("$STOP Y-you don't have permission to see this command's help, b-baka!")
            .queue { it.delete().queueAfter(30, TimeUnit.SECONDS) }

        return
    }

    if (command is ICommand.HelpHandler) {
        command.onHelp(event)
        return
    }

    if (command is ICommand.HelpDialog) {
        event.channel.sendMessage(command.helpEmbed(event)).queue()
        return
    }

    event.channel
        .sendMessage("$ERROR S-sorry, b-but the command doesn't provide any help. I can still pat you, right?")
        .queue { it.delete().queueAfter(30, TimeUnit.SECONDS) }
}