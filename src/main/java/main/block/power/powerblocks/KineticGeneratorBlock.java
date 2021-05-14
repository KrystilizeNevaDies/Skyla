package main.block.power.powerblocks;

import main.block.power.CustomPowerBlock;
import main.block.power.PowerBlock;
import net.minestom.server.data.Data;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

public class KineticGeneratorBlock extends CustomPowerBlock {

	public KineticGeneratorBlock() {
		super(Block.PISTON, "kinetic_generator", 20);
	}
	
	@Override
	public void update(Instance instance, BlockPosition blockPosition, Data data) {
		PowerBlock block = PowerBlock.of(blockPosition, data, this.getClass());
		
		block.setPower(Math.min(block.getPower() + 2.5, block.getMaxPower()));
		
		super.update(instance, blockPosition, data);
	}
}
