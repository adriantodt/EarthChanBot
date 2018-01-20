package xyz.cuteclouds.earthchan.data.entities

import com.rethinkdb.RethinkDB.r
import com.rethinkdb.run
import xyz.cuteclouds.earthchan.Bot
import xyz.cuteclouds.earthchan.data.db.ManagedDatabase
import xyz.cuteclouds.earthchan.data.db.ManagedObject

data class WikiArticle(
    var id: String = ManagedDatabase.ID_WORKER.generate().toString(),
    var pageId: String,
    var page: String
) : ManagedObject {

    override fun delete() {
        r.table(DB_TABLE).get(id)
            .delete()
            .run<Any?>(Bot.pool)
    }

    override fun save() {
        r.table(DB_TABLE).insert(this)
            .optArg("conflict", "replace")
            .run<Any?>(Bot.pool)
    }

    companion object {
        @JvmField
        val DB_TABLE = "wiki"
    }
}
