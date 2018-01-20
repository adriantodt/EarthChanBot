package xyz.cuteclouds.earthchan.extensions

import br.com.brjdevs.java.snowflakes.entities.Config
import br.com.brjdevs.java.snowflakes.entities.Worker
import br.com.brjdevs.java.utils.async.Async
import com.google.inject.Injector
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.MessageBuilder
import net.dv8tion.jda.core.entities.IMentionable
import net.dv8tion.jda.core.entities.MessageEmbed
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

inline operator fun <reified T> Injector.invoke(): T = this(classOf())

operator fun <T> Injector.invoke(c: Class<T>): T = getInstance(c)

operator fun Config.get(datacenterId: Long, workerId: Long): Worker = worker(datacenterId, workerId)

inline fun <reified T> classOf() = T::class.java

fun newTask(name: String, every: Long, unit: TimeUnit, function: () -> Unit): ScheduledExecutorService = Async.task(name, function, every, unit)

operator fun CharSequence.times(amount: Int): String {
    val out = StringBuilder(this)
    repeat(amount) {
        out += this
    }
    return out.toString()
}

val SPACES = Regex("\\s+")

operator fun CharSequence.div(amount: Int): List<String> {
    if (amount < 1)
        return this.split(SPACES)
    return this.split(SPACES, amount)
}

operator fun <V> Future<V>.invoke(): V = get()

operator fun String.rem(col: Collection<Any?>) = rem(col.toTypedArray())
operator fun String.rem(arr: Array<Any?>) = String.format(this, *arr)

fun client(accountType: AccountType, init: JDABuilder.() -> Unit) = with (JDABuilder(accountType)) {
    init()
    buildBlocking()
}

fun shardManager(init: DefaultShardManagerBuilder.() -> Unit) = with (DefaultShardManagerBuilder()) {
    init()
    build()
}

fun embed(init: EmbedBuilder.() -> Unit): MessageEmbed = with (EmbedBuilder()) {
    init()
    build()
}

fun message(init: MessageBuilder.() -> Unit) = with (MessageBuilder()) {
    init()
    build()
}

operator fun Appendable.plusAssign(other: CharSequence) {
    append(other)
}

operator fun Appendable.plusAssign(other: Char) {
    append(other)
}

operator fun Appendable.plusAssign(other: IMentionable) {
    append(other.asMention)
}
