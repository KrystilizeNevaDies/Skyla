package main.block.power.powerblocks;

import main.block.BlockPropertyList;
import main.block.power.CustomPowerBlock;
import main.block.power.PowerBlock;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.Direction;
import net.minestom.server.utils.MathUtils;

public class PowerDiverterBlock extends CustomPowerBlock {
	
	private static final String DIRECTION_KEY = "direction";
	
	public PowerDiverterBlock() {
		super(Block.REPEATER, "power_diverter", 0.0);
	}
	
	@Override
	public boolean shouldSendPower(BlockPosition sendingPosition, Data sendingBlock, BlockPosition receivingPosition, PowerBlock receivingBlock) {
		return false;
	}
	
	private static BlockFace getBlockFace(Direction dir) {
		switch(dir) {
			case EAST:
				return BlockFace.EAST;
			case NORTH:
				return BlockFace.NORTH;
			case SOUTH:
				return BlockFace.SOUTH;
			case WEST:
				return BlockFace.WEST;
			default:
				new Throwable("Invalid direction for power diverter: " + dir).printStackTrace();
				return null;
		}
	}
	
	public void update(Instance instance, BlockPosition blockPosition, Data data) {
		super.update(instance, blockPosition, data);
		
		// Move power from behind to in front
		Direction direction = data.get(DIRECTION_KEY);
		BlockFace acceptingBlockFace = getBlockFace(direction);
		
		BlockPosition aBlockPosition = blockPosition.getRelative(acceptingBlockFace);
		BlockPosition bBlockPosition = blockPosition.getRelative(acceptingBlockFace.getOppositeFace());
		
		CustomBlock aBlock = instance.getCustomBlock(aBlockPosition);
		CustomBlock bBlock = instance.getCustomBlock(bBlockPosition);
		
		if (aBlock instanceof CustomPowerBlock && bBlock instanceof CustomPowerBlock) {
			
			Data aBlockData = instance.getBlockData(aBlockPosition);
			Data bBlockData = instance.getBlockData(bBlockPosition);
			
			PowerBlock aPowerBlock = PowerBlock.of(aBlockPosition, aBlockData, ((CustomPowerBlock) aBlock).getClass());
			PowerBlock bPowerBlock = PowerBlock.of(bBlockPosition, bBlockData, ((CustomPowerBlock) bBlock).getClass());
			
			double aBlockPower = aPowerBlock.getPower();
			double bBlockPower = bPowerBlock.getPower();
			double bBlockMaxPower = bPowerBlock.getMaxPower();
			
			double newPower = aBlockPower + bBlockPower;
			
			if (newPower > bBlockMaxPower) {
				bPowerBlock.setPower(bBlockMaxPower);
				
				aPowerBlock.setPower(newPower - bBlockMaxPower);
			} else {
				bPowerBlock.setPower(newPower);
				
				aPowerBlock.setPower(0);
			}
		}
	}

	@Override
	public short blockStateOnPlacement(Player player, BlockPosition position, Data data) {
		Direction direction = MathUtils.getHorizontalDirection(player.getPosition().getYaw()).opposite();
		
		data.set(DIRECTION_KEY, direction);
		
		return getBaseBlockState()
			.with("delay", "1")
			.with("facing", direction.name().toLowerCase())
			.with("locked", "false")
			.with("powered", "false")
			.getBlockId();
	}

	@Override
	protected BlockPropertyList createPropertyValues() {
		return new BlockPropertyList()
			.property("delay", "1", "2", "3", "4")
			.facingProperty("facing")
			.booleanProperty("locked")
			.booleanProperty("powered");
	}
}