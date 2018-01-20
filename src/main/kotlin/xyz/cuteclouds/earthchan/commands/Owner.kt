package xyz.cuteclouds.earthchan.commands

import br.com.brjdevs.java.utils.collections.CollectionUtils.random
import bsh.Interpreter
import com.rethinkdb.RethinkDB.r
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.core.commands.*
import xyz.cuteclouds.earthchan.utils.commands.HelpFactory
import java.awt.Color
import java.util.*
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

@Command("owner")
class Owner : SimpleArgsCommand(expectedArgs = 1, rest = true), ICommand.Permission, ICommand.HelpDialog {
    private val evals = TreeMap<String, (GuildMessageReceivedEvent, code: String) -> Any?>()

    override val permission = CommandPermission.BOT_OWNER

    init {
        evals["js"] = { event, code ->
            val nashorn = ScriptEngineManager().getEngineByName("nashorn")
            nashorn["db"] = Bot.db
            nashorn["jda"] = event.jda
            nashorn["event"] = event
            nashorn["guild"] = event.guild
            nashorn["channel"] = event.channel
            nashorn["r"] = r
            nashorn["pool"] = Bot.pool

            try {
                nashorn.eval(
                    arrayOf(
                        "imports = new JavaImporter(java.util, java.io, java.net);",
                        "(function() {",
                        "with(imports) {",
                        code,
                        "}",
                        "})()"
                    ).joinToString("\n")
                )
            } catch (e: Exception) {
                e
            }
        }

        evals["bsh"] = { event, code ->
            try {
                val bsh = Interpreter()
                bsh["db"] = Bot.db
                bsh["jda"] = event.jda
                bsh["event"] = event
                bsh["guild"] = event.guild
                bsh["channel"] = event.channel
                bsh["r"] = r
                bsh["pool"] = Bot.pool

                bsh.eval(arrayOf("import *;", code).joinToString("\n"))
            } catch (e: Exception) {
                e
            }
        }
    }

    public override fun call(event: GuildMessageReceivedEvent, args: Array<String>) {
        if (args.isEmpty()) return showHelp()

        val option = args[0]

        if (option == "shutdown") {
            try {
                event.channel.sendMessage(random(Bot.sleepQuotes, Bot.random)).complete()
            } catch (ignored: Exception) {
            }

            System.exit(0)
            return
        }

        if (args.size < 2) showHelp()

        val value = args[1]
        //1 arg

        val values = args(event, value)
        if (values.size < 2) showHelp()

        val k = values[0]
        val v = values[1]
        //2 args

        if (option == "eval") {
            val evaluator = evals[k] ?: return showHelp()

            val result = evaluator(event, v)
            val errored = result is Throwable

            event.channel.sendMessage(EmbedBuilder()
                .setAuthor(
                    "Executado " + if (errored) "e falhou" else "com sucesso", null,
                    event.author.avatarUrl
                )
                .setColor(if (errored) Color.RED else Color.GREEN)
                .setDescription(
                    if (result == null)
                        "Executado com sucesso e nenhum objeto retornado."
                    else
                        "Executado " + (if (errored) "e falhou com a seguinte exceção: " else "com sucesso e retornou: ") + result
                            .toString()
                )
                .setFooter("Executado por: " + event.author.name, null)
                .build()
            ).queue()

            return
        }

        showHelp()
    }

    private val helpFactory = HelpFactory("Owner Command", permission)
        .usage("owner shutdown", "Shutdowns the bot.")
        .usage("owner eval", "Runs arbitrary code.")

    override fun helpEmbed(event: GuildMessageReceivedEvent) = helpFactory.build(event)

    private operator fun ScriptEngine.set(key: String, value: Any?) = put(key, value)
}