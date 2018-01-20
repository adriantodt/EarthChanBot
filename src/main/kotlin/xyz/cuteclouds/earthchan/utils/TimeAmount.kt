package xyz.cuteclouds.earthchan.utils

import java.util.concurrent.TimeUnit

data class TimeAmount(val amount: Long, val unit: TimeUnit) {

    override fun toString(): String {
        return "$amount ${unit.toString().toLowerCase()}"
    }

    fun compress(): TimeAmount {
        return convertTo(
            TimeUnit.values().last {
                //If do back and forth conversion is lossless
                unit.convert(it.convert(amount, unit), it) == amount
            }
        )
    }

    fun convertTo(newUnit: TimeUnit): TimeAmount {
        return if (unit == newUnit) this else TimeAmount(newUnit.convert(amount, unit), newUnit)
    }

    @Throws(InterruptedException::class)
    fun sleep() {
        unit.sleep(amount)
    }

    @Throws(InterruptedException::class)
    fun timedJoin(thread: Thread) {
        unit.timedJoin(thread, amount)
    }

    @Throws(InterruptedException::class)
    fun timedWait(obj: Any) {
        unit.timedWait(obj, amount)
    }

    fun toDays(): Long {
        return unit.toDays(amount)
    }

    fun toHours(): Long {
        return unit.toHours(amount)
    }

    fun toMicros(): Long {
        return unit.toMicros(amount)
    }

    fun toMillis(): Long {
        return unit.toMillis(amount)
    }

    fun toMinutes(): Long {
        return unit.toMinutes(amount)
    }

    fun toNanos(): Long {
        return unit.toNanos(amount)
    }

    fun toSeconds(): Long {
        return unit.toSeconds(amount)
    }

    companion object {
        fun normalize(vararg amounts: TimeAmount): Array<TimeAmount> {
            val unit = amounts.map { it.unit }.min() ?: TimeUnit.values()[0]
            return amounts.map { it.convertTo(unit) }.toTypedArray()
        }

        fun sum(vararg amounts: TimeAmount): TimeAmount {
            val normal = normalize(*amounts.map(TimeAmount::compress).toTypedArray())
            val sum = normal.map { it.amount }.sum()
            return TimeAmount(sum, normal[0].unit).compress()
        }
    }
}
