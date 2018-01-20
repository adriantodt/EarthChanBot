package xyz.cuteclouds.earthchan.data.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import xyz.cuteclouds.earthchan.extensions.classOf
import xyz.cuteclouds.hunger.loader.string
import xyz.cuteclouds.hunger.loader.write
import java.io.File
import java.io.IOException
import javax.inject.Provider

object ConfigManager : Provider<Config> {
    override fun get() = config

    val config: Config by lazy {
        try {
            mapper.readValue<Config>(path.string(), classOf())
        } catch (e: IOException) {
            save(Config())
            throw e
        }
    }

    fun save() = save(config)

    private val mapper = ObjectMapper().registerKotlinModule()
    private val path = File("config.json").toPath()

    private fun save(config: Config) = path.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(config))
}
