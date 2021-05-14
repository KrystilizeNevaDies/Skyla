package main

import main.SkylaExtension
import main.block.BlockSystem.init
import net.minestom.server.MinecraftServer
import net.minestom.server.extensions.Extension
import net.minestom.server.instance.Instance
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class SkylaExtension : Extension() {
    override fun preInitialize() {
        LOGGER = logger
    }

    override fun initialize() {
        LOGGER!!.info("Starting Skyla Extension")
        LOGGER!!.info("Initializing blocks system...")
        init()
        MinecraftServer.getInstanceManager().instances.forEach(Consumer { instance: Instance ->
            initializeInstance(
                instance
            )
        })
    }

    override fun postInitialize() {}
    override fun terminate() {}

    companion object {
		fun runStandalone() {
            LOGGER = LoggerFactory.getLogger(SkylaExtension::class.java)
            val extension = SkylaExtension()
            extension.initialize()
            extension.postInitialize()
        }

		var LOGGER: Logger? = null
        private fun initializeInstance(instance: Instance) {}
    }
}