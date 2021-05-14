package main.block.power.powerblocks;

import main.block.BlockPropertyList;
import main.block.power.CustomPowerBlock;
import main.block.power.PowerBlock;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

public class WireBlock extends CustomPowerBlock {
	public WireBlock() {
		super(Block.REDSTONE_WIRE, "wire", 50.0);
	}
	
	@Override
	public void update(Instance instance, BlockPosition blockPosition, Data data) {
		super.update(instance, blockPosition, data);
		
		PowerBlock block = PowerBlock.of(blockPosition, data, WireBlock.class);
		
		short blockStateID = instance.getBlockStateId(blockPosition);
		
		short newBlockStateID = withPower(block.getPower() / block.getMaxPower());
		
		if (blockStateID != newBlockStateID) {
			instance.refreshBlockStateId(blockPosition, newBlockStateID);
		}
	}
	
	private short withPower(double power) {
		
		int powerLevel = (int) Math.floor(power * 15);
		
		return getBaseBlockState()
			.with("east", "side")
			.with("north", "side")
			.with("power", String.valueOf(powerLevel))
			.with("south", "side")
			.with("west", "side")
			.getBlockId();
	}
	
	@Override
	public short blockStateOnPlacement(Player player, BlockPosition position, Data data) {
		return getBaseBlockState()
			.with("east", "side")
			.with("north", "side")
			.with("power", "0")
			.with("south", "side")
			.with("west", "side")
			.getBlockId();
	}

	@Override
	protected BlockPropertyList createPropertyValues() {
		return new BlockPropertyList()
			.property("east", "none", "side", "up")
			.property("north", "none", "side", "up")
			.intRange("power", 0, 15)
			.property("south", "none", "side", "up")
			.property("west", "none", "side", "up");
	}
}