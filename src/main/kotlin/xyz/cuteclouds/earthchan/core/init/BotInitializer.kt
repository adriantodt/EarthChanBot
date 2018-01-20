package xyz.cuteclouds.earthchan.core.init

import com.google.inject.Injector
import org.reflections.Reflections
import xyz.cuteclouds.earthchan.core.CommandProcessorAndRegistry
import xyz.cuteclouds.earthchan.core.commands.Command
import xyz.cuteclouds.earthchan.core.commands.ICommand
import xyz.cuteclouds.earthchan.extensions.classOf
import xyz.cuteclouds.earthchan.extensions.invoke

class BotInitializer(
    reflections: Reflections
) {
    constructor(basePackage: String) : this(Reflections(basePackage))

    private val commandClasses: Set<Class<out ICommand>>

    init {
        this.commandClasses = reflections.getSubTypesOf<ICommand>(classOf())
            .filterTo(HashSet()) { it.isAnnotationPresent(classOf<Command>()) }
    }

    fun initialize(injector: Injector) {
        val registry: CommandProcessorAndRegistry = injector()

        commandClasses.forEach {
            val command: ICommand = injector(it)
            for (k in it.getAnnotation<Command>(classOf()).value) registry.register(k, command)
        }
    }
}
