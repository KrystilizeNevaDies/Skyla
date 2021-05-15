package main

import main.block.BlockSystem
import net.minestom.server.extensions.Extension
import org.slf4j.Logger

class SkylaExtension : Extension() {
    override fun preInitialize() {
        LOGGER = logger
    }

    override fun initialize() {
        LOGGER!!.info("Starting Skyla Extension")
        LOGGER!!.info("Initializing blocks system...")
        BlockSystem.init()
    }

    override fun postInitialize() {}
    override fun terminate() {}

    companion object {
		var LOGGER: Logger? = null
    }
}