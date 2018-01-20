package xyz.cuteclouds.earthchan.core

import xyz.cuteclouds.earthchan.core.commands.ICommand

interface CommandRegistry {
    val commands: Map<String, ICommand>

    fun register(s: String, c: ICommand)
}
