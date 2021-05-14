package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.block.BlockSystem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.Instance;

public class SkylaExtension extends Extension {
	
	public static void runStandalone() {
		
		LOGGER = LoggerFactory.getLogger(SkylaExtension.class);
		
		SkylaExtension extension = new SkylaExtension();
		extension.initialize();
		extension.postInitialize();
	}
	
	public static Logger LOGGER;
	
	@Override
	public void preInitialize() {
		LOGGER = this.getLogger();
	}
	
	@Override
	public void initialize() {
		LOGGER.info("Starting Skyla Extension");
		
		LOGGER.info("Initializing blocks system...");
		
		BlockSystem.init();
		
		MinecraftServer.getInstanceManager().getInstances().forEach(SkylaExtension::initializeInstance);
	}
	
	@Override
	public void postInitialize() {
		
	}

	@Override
	public void terminate() {
		
	}
	
	private static void initializeInstance(Instance instance) {
		
	}
}
