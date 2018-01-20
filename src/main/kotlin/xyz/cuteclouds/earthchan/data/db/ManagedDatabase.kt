package xyz.cuteclouds.earthchan.data.db

import br.com.brjdevs.java.snowflakes.Snowflakes
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.net.Cursor
import com.rethinkdb.pool.ConnectionPool
import com.rethinkdb.run
import xyz.cuteclouds.earthchan.data.entities.WikiArticle
import xyz.cuteclouds.earthchan.extensions.classOf
import xyz.cuteclouds.earthchan.extensions.get
import xyz.cuteclouds.earthchan.utils.BotUtils

class ManagedDatabase(private val conn: ConnectionPool) {

    fun getLastWiki(): List<WikiArticle> {
        return r.table(WikiArticle.DB_TABLE)
            .orderBy("id")
            .limit(6)
            .run(conn, classOf<WikiArticle>())
    }

    fun getWikiByPageId(pageId: String): List<WikiArticle> {
        val c = r.table(WikiArticle.DB_TABLE)
            .getAll(pageId).optArg("index", "pageId")
            .limit(6)
            .run<Cursor<WikiArticle>>(conn, classOf<WikiArticle>())

        return c.toList()
    }

    fun searchWiki(term: String): List<WikiArticle> {
        val pattern = BotUtils.escapeRegex(term.toLowerCase())

        val c = r.table(WikiArticle.DB_TABLE)
            .filter { article -> article.g("page").downcase().match(pattern) }
            .run<Cursor<WikiArticle>>(conn, classOf<WikiArticle>())

        return c.toList()
    }

    companion object {
        val FACTORY = Snowflakes.config(1495900000L, 2L, 2L, 12L)
        val ID_WORKER = FACTORY[0, 1]
        val LOG_WORKER = FACTORY[0, 2]
    }
}
