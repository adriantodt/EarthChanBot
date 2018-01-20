package xyz.cuteclouds.earthchan.utils

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.utils.MiscUtil
import javax.inject.Inject

class FakeUserCache
@Inject constructor(
    private val jda: JDA
) {
    private val fakeUserCache = MiscUtil.newLongMap<User>()

    fun asyncCache(vararg ids: Long) {
        for (id in ids) jda.retrieveUserById(id).queue { fakeUserCache.put(id, it) }
    }

    fun removeFromCache(vararg ids: Long) {
        for (id in ids) fakeUserCache.remove(id)
    }

    fun retrieveUserById(id: Long): User {
        if (fakeUserCache.containsKey(id)) {
            return fakeUserCache[id]
        }

        val user = jda.retrieveUserById(id).complete()

        if (user.isFake) fakeUserCache.put(id, user)

        return user
    }
}
