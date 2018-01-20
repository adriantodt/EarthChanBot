package xyz.cuteclouds.jdakaiperscript

import net.dv8tion.jda.core.entities.Member
import xyz.avarel.kaiper.runtime.Bool
import xyz.avarel.kaiper.runtime.Obj
import xyz.avarel.kaiper.runtime.collections.Array
import xyz.avarel.kaiper.runtime.java.JavaObject

class SafeMentions(mentionedMembers: List<Member>) : Array() {
    init {
        mentionedMembers.map(::SafeMember).map(::JavaObject).forEach( { add(it) })
    }

    override fun getAttr(name: String): Obj {
        return when (name) {
            "isEmpty" -> Bool.of(isEmpty())
            "first" -> get(0)
            "last" -> get(size - 1)
            else -> super.getAttr(name)
        }
    }
}
