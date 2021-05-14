package main.block;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.SkylaExtension;
import main.block.power.powerblocks.BatteryBlock;
import main.block.power.powerblocks.KineticGeneratorBlock;
import main.block.power.powerblocks.ManualGeneratorBlock;
import main.block.power.powerblocks.PowerDiverterBlock;
import main.block.power.powerblocks.WireBlock;
import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.data.DataImpl;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;

public class BlockSystem {
	
	private static Map<Block, CustomBlock> blockMap = new HashMap<>();
	private static Set<CustomBlock> customBlocks = new LinkedHashSet<>();
	
	
	public static void init() {
		BlockManager manager = MinecraftServer.getBlockManager();
		
		for (CustomBlock block : 
				List.of(
					new WireBlock(),
					new ManualGeneratorBlock(),
					new BatteryBlock(),
					new PowerDiverterBlock(),
					new KineticGeneratorBlock()
				)
			) {
			
			Block staticBlock = Block.fromStateId(block.getDefaultBlockStateId());
			
			SkylaExtension.LOGGER.info("Registering static block " + staticBlock + " to custom block: " + block);
			
			blockMap.put(staticBlock, block);
			customBlocks.add(block);
			
			manager.registerCustomBlock(block);
		}
		
		MinecraftServer.getGlobalEventHandler().addEventCallback(PlayerBlockPlaceEvent.class, (event) -> {
			CustomBlock block = blockMap.get(Block.fromStateId(event.getBlockStateId()));
			
			if (block != null) {
				
				Data data = new DataImpl();
				
				event.setCustomBlock(block);
				event.setBlockData(data);
				
				if (block instanceof SkylaCustomBlock)
					event.setBlockStateId(((SkylaCustomBlock) block).blockStateOnPlacement(event.getPlayer(), event.getBlockPosition(), data));
				
				
				
			}
		});
	}

	/**
	 * Slow method of retrieving a truly unique ID for each custom block.
	 * @param customBlock
	 * @return unique id
	 */
	public static short getCustomBlockId(CustomBlock customBlock) {
		
		short id = 1;
		
		for (CustomBlock block : customBlocks) {
			if (block == customBlock)
				return id;
			id++;
		}
		
		new Throwable("Custom Block: " + customBlock + " not found in block system.").printStackTrace();
		return 0;
	}
}
