package xyz.cuteclouds.earthchan

import com.rethinkdb.RethinkDB
import com.rethinkdb.pool.ConnectionPool
import okhttp3.OkHttpClient
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyConfig
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyDb
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyExecutor
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyHttpClient
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyPool
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyRandom
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.data.config.ConfigManager
import xyz.cuteclouds.earthchan.data.db.ManagedDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object Bot {

    //Database
    val config by lazyConfig
    val pool: ConnectionPool by lazyPool
    val db: ManagedDatabase by lazyDb

    //Versioning
    val DEV_MODE = !java.lang.Boolean.parseBoolean("@false@".replace("@", ""))
    val VERSION = if (DEV_MODE) "DEV" + SimpleDateFormat("ddMMyyyy").format(Date()) else "@version@"

    //Shared Objects
    val random: Random by lazyRandom
    val httpClient: OkHttpClient by lazyHttpClient
    val executor: ScheduledExecutorService by lazyExecutor

    //Lists
    val owners = listOf(
        "217747278071463937"
    )

    val bootQuotes = File("assets/earthchan/boot_quotes.txt").readLines()
    val sleepQuotes =  File("assets/earthchan/sleep_quotes.txt").readLines()
    val splashes =  File("assets/earthchan/splashes.txt").readLines()
}

object LazyBotObjects {
    val lazyConfig = lazy { ConfigManager.config }
    val lazyPool = lazy { RethinkDB.r.connectionPool(Bot.config.database.configure()) }
    val lazyDb = lazy { ManagedDatabase(Bot.pool) }
    val lazyRandom = lazy { Random() }
    val lazyHttpClient = lazy { OkHttpClient() }
    val lazyExecutor = lazy { Executors.newSingleThreadScheduledExecutor() }
    val lazyRegistry = lazy { CommandProcessorAndRegistry(Bot.config) }
}