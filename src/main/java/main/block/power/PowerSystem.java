package main.block.power;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import main.SkylaExtension;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.chunk.ChunkUtils;

// Defines the behavior of the custom power system for one instance
public class PowerSystem {
	
	private static final Map<Instance, PowerSystem> powerSystems = new HashMap<>();
	
	private final Long2ObjectMap<PowerBlock[]> powerGrid = new Long2ObjectOpenHashMap<>();
	
	final Instance instance;
	
	public static PowerSystem of(Instance instance) {
		if (powerSystems.containsKey(instance))
			return powerSystems.get(instance);
		
		PowerSystem powerSystem = new PowerSystem(instance);
		
		powerSystems.put(instance, powerSystem);
		
		return powerSystem;
	}
	
	private PowerSystem(Instance instance) {
		this.instance = instance;
	}
	
	public void setPowerBlock(long chunk, int pos, PowerBlock block) {
		indexWireGrid(chunk)[pos] = block;
	}
	
	@Nullable
	public PowerBlock getPowerBlock(long chunk, int pos) {
		return indexWireGrid(chunk)[pos];
	}
	
	public PowerBlock getPowerBlock(BlockPosition blockPosition) {
		int chunkX = ChunkUtils.getChunkCoordinate(blockPosition.getX());
		int chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.getZ());
		long chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ);
		int positionIndex = ChunkUtils.getBlockIndex(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
		
		return indexWireGrid(chunk)[positionIndex];
	}
	
	private PowerBlock[] indexWireGrid(long chunk) {
		PowerBlock[] chunkGrid = powerGrid.get(chunk);
		
		if (chunkGrid != null)
			return chunkGrid;
		
		SkylaExtension.LOGGER.info("Creating new power grid for chunk: " + chunk);
		
		chunkGrid = new PowerBlock[16 * 256 * 16];
		
		powerGrid.put(chunk, chunkGrid);
		
		return chunkGrid;
	}

	/**
	 * Registers a block
	 */
	public void registerBlock(long chunk, int pos, PowerBlock block) {
		indexWireGrid(chunk)[pos] = block;
	}
	
	/**
	 * Unregisters a block
	 */
	public void unregisterBlock(long chunk, int pos, PowerBlock block) {
		indexWireGrid(chunk)[pos] = null;
	}
}
