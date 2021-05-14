package main.block.power;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import main.block.VisualCustomBlock;
import main.block.power.powerblocks.PowerDiverterBlock;
import net.kyori.adventure.text.Component;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;

public abstract class CustomPowerBlock extends VisualCustomBlock {
	
	public CustomPowerBlock(@NotNull Block block, @NotNull String identifier, double maxPower) {
		this(block.getBlockId(), identifier, maxPower);
	}

	public CustomPowerBlock(short defaultBlockStateId, @NotNull String identifier, double maxPower) {
		super(defaultBlockStateId, identifier);
		
		PowerBlock.registerPowerBlock(this.getClass(), maxPower);
	}
	
	@Override
	public void onPlace(@NotNull Instance instance, @NotNull BlockPosition blockPosition, @Nullable Data data) {
		PowerSystem system = PowerSystem.of(instance);
		int chunkX = ChunkUtils.getChunkCoordinate(blockPosition.getX());
		int chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.getZ());
		long chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ);
		int pos = ChunkUtils.getBlockIndex(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
		
		system.registerBlock(chunk, pos, PowerBlock.of(blockPosition, data, this.getClass()));
	}
	
	@Override
	public void onDestroy(@NotNull Instance instance, @NotNull BlockPosition blockPosition, @Nullable Data data) {
		PowerSystem system = PowerSystem.of(instance);
		int chunkX = ChunkUtils.getChunkCoordinate(blockPosition.getX());
		int chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.getZ());
		long chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ);
		int pos = ChunkUtils.getBlockIndex(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
		
		system.unregisterBlock(chunk, pos, PowerBlock.of(blockPosition, data, this.getClass()));
	}
	
	@Override
	public void update(Instance instance, BlockPosition blockPosition, Data data) {
		PowerSystem system = PowerSystem.of(instance);
		int posX = blockPosition.getX();
		int posY = blockPosition.getY();
		int posZ = blockPosition.getZ();
		
		PowerBlock block = PowerBlock.of(blockPosition, data, this.getClass());
		
		for (int offset : List.of(-1, 1)) {
			BlockPosition[] positions = {
				new BlockPosition(posX + offset, posY, posZ),
				new BlockPosition(posX, posY + offset, posZ),
				new BlockPosition(posX, posY, posZ + offset),
			};
			
			Set<PowerBlock> blockSet = new HashSet<>();
			
			for (BlockPosition adjacentBlockPosition : positions) {
				PowerBlock adjacentBlock = system.getPowerBlock(adjacentBlockPosition);
				
				if (adjacentBlock != null && shouldSendPower(blockPosition, data, adjacentBlockPosition, adjacentBlock)) {
					blockSet.add(adjacentBlock);
				}
			}
			
			handleInteraction(block, data, blockSet);
		}
	}
	
	protected void handleInteraction(PowerBlock aBlock, Data aBlockData, Set<PowerBlock> blockSet) {
		double blockPower = aBlock.getPower();
		double size = blockSet.size() + 1;
		
		double averagePower = blockPower / size;
		
		double leftOverPower = 0.0;
		
		for (PowerBlock bBlock : blockSet) {
			
			double newBlockPower = bBlock.getPower() + averagePower;
			
			if (bBlock.getMaxPower() < newBlockPower) {
				double residual = newBlockPower - bBlock.getMaxPower();
				
				bBlock.setPower(bBlock.getMaxPower());
				
				leftOverPower += residual;
			} else {
				bBlock.setPower(newBlockPower);
			}
		}
		
		aBlock.setPower(averagePower + leftOverPower);
	}
	
	public boolean shouldSendPower(BlockPosition sendingPosition, Data sendingBlock, BlockPosition receivingPosition, PowerBlock receivingBlock) {
		return !receivingBlock.getBlockClass().equals(PowerDiverterBlock.class);
	}
	
	@Override
	public boolean onInteract(@NotNull Player player, @NotNull Hand hand, @NotNull BlockPosition blockPosition,
			@Nullable Data data) {
		
		if (player.isSneaking())
			return false;
		
		PowerBlock block = PowerBlock.of(blockPosition, data, this.getClass());
		
		player.sendActionBar(Component.text("Power: " + block.getPower()));
		
		return true;
	}
	
	@Override
	public UpdateOption getUpdateOption() {
		return new UpdateOption(5, TimeUnit.TICK);
	}
	
	@Override
	public short getCustomBlockId() {
		return (short) Math.abs((short) this.getIdentifier().hashCode());
	}
}
