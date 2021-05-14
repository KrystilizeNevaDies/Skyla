package main.block;

import java.util.List;

import main.block.power.powerblocks.BatteryBlock;
import main.block.power.powerblocks.ManualGeneratorBlock;
import main.block.power.powerblocks.PowerDiverterBlock;
import main.block.power.powerblocks.WireBlock;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;

public class BlockSystem {
	public static void init() {
		BlockManager manager = MinecraftServer.getBlockManager();
		
		for (CustomBlock block : 
				List.of(
					new WireBlock(),
					new ManualGeneratorBlock(),
					new BatteryBlock(),
					new PowerDiverterBlock()
				)
			) {
			manager.registerCustomBlock(block);
		}
	}
}
