package xyz.cuteclouds.earthchan.data.config

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import java.util.*

data class Config(
    var tokens: Tokens = Tokens(),
    var channels: Channels = Channels(),
    var database: DbConfig = DbConfig(),

    var prefixes: MutableList<String> = LinkedList(),
    var developers: MutableList<String> = LinkedList(),
    var donors: MutableList<String> = LinkedList(),
    var blacklist: MutableList<String> = LinkedList()
) {
    fun addDeveloper(id: String) {
        developers.add(id)
    }

    fun removeDeveloper(id: String) {
        developers.remove(id)
    }

    fun addDonor(id: String) {
        donors.add(id)
    }

    fun removeDonor(id: String) {
        donors.remove(id)
    }

    fun blacklist(id: String) {
        blacklist.add(id)
    }

    fun unBlacklist(id: String) {
        blacklist.remove(id)
    }
}

data class Channels(
    var logging: String? = null
)

data class Tokens(
    var discord: String? = null,
    var weebSh: String? = null,
    var botlist: String? = null
)

data class DbConfig(
    var hostname: String? = null,
    var dbname: String? = null,
    var port: Int? = null,
    var user: String? = null,
    var password: String? = null
) {

    fun configure(): Connection.Builder {
        val b = RethinkDB.r.connection()
        if (hostname != null) b.hostname(hostname!!)
        if (hostname != null) b.db(dbname!!)
        if (port != null) b.port(port!!)
        if (user != null) b.user(user!!, password!!)
        return b
    }
}

