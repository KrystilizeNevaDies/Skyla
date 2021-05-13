package main.block;

import main.block.power.powerblocks.WireBlock;
import net.minestom.server.MinecraftServer;

public class BlockSystem {
	public static void init() {
		MinecraftServer.getBlockManager().registerCustomBlock(new WireBlock());
	}
}
