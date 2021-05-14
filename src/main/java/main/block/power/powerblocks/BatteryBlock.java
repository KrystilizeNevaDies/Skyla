package main.block.power.powerblocks;

import main.block.power.CustomPowerBlock;
import net.minestom.server.instance.block.Block;

public class BatteryBlock extends CustomPowerBlock {
	public BatteryBlock() {
		super(Block.REDSTONE_BLOCK, "battery", 10000.0);
	}
}