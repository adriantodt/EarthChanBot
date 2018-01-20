package xyz.cuteclouds.earthchan.commands.info

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.core.commands.*
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.availableProcessors
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.cpuUsage
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.freeMemory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.maxMemory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.threadCount
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.totalMemory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.vpsCPUUsage
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.vpsFreeMemory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.vpsMaxMemory
import xyz.cuteclouds.earthchan.utils.helpers.AsyncInfoMonitor.vpsUsedMemory
import xyz.cuteclouds.earthchan.utils.helpers.CommandStatsManager
import xyz.cuteclouds.earthchan.utils.helpers.CommandStatsManager.Type
import xyz.cuteclouds.earthchan.utils.helpers.CommandStatsManager.Type.*

@Command("stats")
class Stats : SimpleArgsCommand(expectedArgs = 2), ICommand.HelpDialog {
    override fun call(event: GuildMessageReceivedEvent, args: Array<String>) {
        when (args.getOrNull(0)) {
            null -> showStats(event)
            "cmds" -> showCmdStats(event, args.getOrNull(1))
            else -> showHelp()
        }
    }

    private fun showCmdStats(event: GuildMessageReceivedEvent, arg: String?) {
        when (arg) {
            null -> showGeneralCmdStats(event)
            "total" -> showSpecificCmdStats(event, "Total", TOTAL)
            "daily" -> showSpecificCmdStats(event, "Today", DAY)
            "hourly" -> showSpecificCmdStats(event, "This Hour", HOUR)
            "now" -> showSpecificCmdStats(event, "Now", MINUTE)
            else -> showHelp()
        }
    }

    private fun showGeneralCmdStats(event: GuildMessageReceivedEvent) {
        event.channel.sendMessage(baseEmbed(event, "Command Stats")
            .addField("Now", CommandStatsManager.resume(MINUTE), false)
            .addField("This Hour", CommandStatsManager.resume(HOUR), false)
            .addField("Today", CommandStatsManager.resume(DAY), false)
            .addField("Total", CommandStatsManager.resume(TOTAL), false)
            .build()
        ).queue()
    }

    private fun showSpecificCmdStats(event: GuildMessageReceivedEvent, display: String, type: Type) {
        event.channel.sendMessage(
            CommandStatsManager.fillEmbed(
                baseEmbed(event, "Command Stats | $display"), type
            )
        ).queue()
    }

    private fun showStats(event: GuildMessageReceivedEvent) {
        event.channel.sendMessage(
            baseEmbed(event, "Earth-chan Bot | Session Stats")
                .addField(
                    "Resource Usage:",
                    arrayOf(
                        "**Threads**: $threadCount",
                        "**RAM**: ${totalMemory - freeMemory}MB/${maxMemory}MB",
                        "**Allocated Memory**: ${totalMemory}MB (${freeMemory}MB remaining)",
                        "**CPU Usage**: $cpuUsage%"
                    ).joinToString("\n"),
                    false
                )
                .addField(
                    "Server:",
                    arrayOf(
                        "**RAM** (Total/Free/Used): ${vpsMaxMemory}GB/${vpsFreeMemory}GB/${vpsUsedMemory}GB",
                        "**CPU Cores**: $availableProcessors cores",
                        "**CPU Usage**: $vpsCPUUsage%"
                    ).joinToString("\n"),
                    false
                )
                .build()
        ).queue()
    }

    private val helpFactory = HelpFactory("Stats Command")
        .description("Shows this bot's stats.")
        .usage("stats", "Shows this session's stats.")
        .usage("stats cmds", "Shows this session's commands stats.")
        .usage("stats cmds <now/hourly/dialy/total>", "Shows detailed info about command usage.")

    override fun helpEmbed(event: GuildMessageReceivedEvent) = helpFactory.build(event)
}