package xyz.cuteclouds.earthchan.core.injections

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provider
import com.google.inject.name.Names
import net.dv8tion.jda.core.JDA
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyConfig
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyDb
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyExecutor
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyHttpClient
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyPool
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyRandom
import xyz.cuteclouds.earthchan.LazyBotObjects.lazyRegistry
import xyz.cuteclouds.earthchan.data.config.ConfigManager
import xyz.cuteclouds.earthchan.extensions.classOf
import java.beans.IntrospectionException
import java.beans.Introspector
import java.lang.reflect.InvocationTargetException

class BotInjections(
    private val jda: JDA
) : AbstractModule() {

    override fun configure() {
        val config = ConfigManager.config

        bindInstance(jda)

        bindLazy(lazyConfig)
        bindLazy(lazyPool)
        bindLazy(lazyDb)
        bindLazy(lazyRandom)
        bindLazy(lazyHttpClient)
        bindLazy(lazyExecutor)
        bindLazy(lazyRegistry)

        mapConstants(config.tokens, "token")
        mapConstants(config.channels, "channel")
    }

    private fun mapConstants(obj: Any?, prefix: String) {
        try {
            for (p in Introspector.getBeanInfo(obj!!.javaClass).propertyDescriptors) {
                val result = p.readMethod(obj) ?: continue

                @Suppress("UNCHECKED_CAST")
                bind(p.propertyType as Class<Any>)
                    .annotatedWith(Names.named("$prefix.${p.name}"))
                    .toInstance(result)
            }
        } catch (e: IntrospectionException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    fun toInjector(): Injector = Guice.createInjector(this)

    private inline fun <reified T> bindInstance(t: T) = bind<T>(classOf()).toInstance(t)
    private inline fun <reified T> bindLazy(lazy: Lazy<T>) = bind<T>(classOf()).toProvider(Provider(lazy::value))

}
