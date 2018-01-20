package xyz.cuteclouds.earthchan.utils.helpers

import br.com.brjdevs.java.utils.async.threads.builder.ThreadBuilder
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.MessageEmbed
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object CommandStatsManager {
    private val ACTIVE_BLOCK = '\u2588'
    private val EMPTY_BLOCK = '\u200b'

    private val EXECUTOR = Executors.newSingleThreadScheduledExecutor(ThreadBuilder().setName("CommandStatsManager Executor"))

    private val TOTAL_CMDS = ConcurrentHashMap<String, AtomicInteger>()
    private val DAY_CMDS = ConcurrentHashMap<String, AtomicInteger>()
    private val HOUR_CMDS = ConcurrentHashMap<String, AtomicInteger>()
    private val MINUTE_CMDS = ConcurrentHashMap<String, AtomicInteger>()

    enum class Type {
        TOTAL {
            override val value: Map<String, AtomicInteger> = TOTAL_CMDS
        },
        DAY {
            override val value: Map<String, AtomicInteger> = DAY_CMDS
        },
        HOUR {
            override val value: Map<String, AtomicInteger> = HOUR_CMDS
        },
        MINUTE {
            override val value: Map<String, AtomicInteger> = MINUTE_CMDS
        };

        internal abstract val value: Map<String, AtomicInteger>
    }

    fun bar(percent: Int, total: Int): String {
        val activeBlocks = (percent.toFloat() / 100f * total).toInt()
        val builder = StringBuilder().append('`').append(EMPTY_BLOCK)
        for (i in 0 until total) builder.append(if (activeBlocks > i) ACTIVE_BLOCK else ' ')
        return builder.append(EMPTY_BLOCK).append('`').toString()
    }

    fun fillEmbed(builder: EmbedBuilder, type: Type): MessageEmbed {
        val commands = type.value

        val total = commands.values.map(AtomicInteger::get).sum()

        if (total == 0) {
            builder.addField("Nada aqui.", "Apenas poeira.", false)
            return builder.build()
        }

        commands.entries
            .map { it.key to it.value.get() }
            .filter { it.second > 0 }
            .sortedByDescending(Pair<String, Int>::second)
            .take(12)
            .forEach {
                val (k, v) = it
                val percent = v * 100 / total
                builder.addField(
                    k, String.format("%s %d%% (%d)", bar(percent, 15), percent, k),
                    true
                )
            }

        return builder.build()
    }

    fun log(cmd: String) {
        if (cmd.isEmpty()) return

        TOTAL_CMDS.computeIfAbsent(cmd) { AtomicInteger() }.incrementAndGet()
        DAY_CMDS.computeIfAbsent(cmd) { AtomicInteger() }.incrementAndGet()
        HOUR_CMDS.computeIfAbsent(cmd) { AtomicInteger() }.incrementAndGet()
        MINUTE_CMDS.computeIfAbsent(cmd) { AtomicInteger() }.incrementAndGet()

        EXECUTOR.schedule({ MINUTE_CMDS[cmd]?.decrementAndGet() }, 1, TimeUnit.MINUTES)
        EXECUTOR.schedule({ HOUR_CMDS[cmd]?.decrementAndGet() }, 1, TimeUnit.HOURS)
        EXECUTOR.schedule({ DAY_CMDS[cmd]?.decrementAndGet() }, 1, TimeUnit.DAYS)
    }

    fun resume(type: Type): String {
        val commands = type.value

        val total = commands.values.map(AtomicInteger::get).sum()

        return if (total == 0)
            "Nothing here, just dust."
        else
            "Total: $total\n" + commands.entries
                .map { it.key to it.value.get() }
                .filter { it.second > 0 }
                .sortedByDescending(Pair<String, Int>::second)
                .take(5)
                .joinToString("\n") {
                val (k, v) = it
                val percent = Math.round(v.toFloat() * 100 / total)
                String.format(
                    "%s %d%% **%s** (%d)", bar(percent, 15), percent, k, v
                )
            }
    }
}
