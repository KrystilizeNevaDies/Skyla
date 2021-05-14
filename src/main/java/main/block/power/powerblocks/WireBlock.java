package main.block.power.powerblocks;

import main.block.power.CustomPowerBlock;
import net.minestom.server.instance.block.Block;

public class WireBlock extends CustomPowerBlock {
	public WireBlock() {
		super(Block.REDSTONE_WIRE, "wire", 50.0);
	}
}