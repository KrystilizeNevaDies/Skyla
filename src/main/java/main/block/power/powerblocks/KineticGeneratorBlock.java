package main.block.power.powerblocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import main.block.power.CustomPowerBlock;
import main.block.power.PowerBlock;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;

public class KineticGeneratorBlock extends CustomPowerBlock {

	public KineticGeneratorBlock() {
		super(Block.BELL, "kinetic_generator", 20);
	}
	
	@Override
	public boolean onInteract(@NotNull Player player, @NotNull Hand hand, @NotNull BlockPosition blockPosition,
			@Nullable Data data) {
		if (player.isSneaking())
			return false;
		
		PowerBlock block = PowerBlock.of(blockPosition, data, this.getClass());
		
		block.setPower(Math.min(block.getPower() + 2.5, block.getMaxPower()));
		
		return super.onInteract(player, hand, blockPosition, data);
	}
}
