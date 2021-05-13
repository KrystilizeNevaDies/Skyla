package main.block.power.powerblocks;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import main.SkylaExtension;
import main.block.power.PowerSystem;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.Player.Hand;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.chunk.ChunkUtils;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;

public abstract class CustomPowerBlock extends CustomBlock {

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
		int chunkX = ChunkUtils.getChunkCoordinate(blockPosition.getX());
		int chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.getZ());
		long chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ);
		
		PowerBlock block = PowerBlock.of(blockPosition, data, WireBlock.class);
		
		SkylaExtension.LOGGER.info(blockPosition + "'s power: " + block.getPower());
		
		for (int offset : List.of(-1, 1)) {
			int[] positions = {
				ChunkUtils.getBlockIndex(blockPosition.getX() + offset, blockPosition.getY(), blockPosition.getZ()),
				ChunkUtils.getBlockIndex(blockPosition.getX(), blockPosition.getY() + offset, blockPosition.getZ()),
				ChunkUtils.getBlockIndex(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ() + offset),
			};
			
			for (int pos : positions) {
				PowerBlock adjacentBlock = system.getPowerBlock(chunk, pos);
				
				if (adjacentBlock != null) {
					handleInteraction(block, adjacentBlock);
					
				}
			}
		}
	}
	
	private static void handleInteraction(PowerBlock aBlock, PowerBlock bBlock) {
		double aBlockPower = aBlock.getPower();
		double bBlockPower = bBlock.getPower();
		double bBlockMaxPower = bBlock.getMaxPower();
		
		double power = aBlockPower / 2.0;
		
		power += bBlockPower;
		
		if (power > bBlockMaxPower) {
			power -= bBlockMaxPower;
			bBlock.setPower(bBlockMaxPower);
			
			aBlock.setPower((aBlockPower / 2.0) + power);
		} else {
			bBlock.setPower(power);
			
			aBlock.setPower(aBlockPower / 2.0);
		}
	}
	
	@Override
	public boolean onInteract(@NotNull Player player, @NotNull Hand hand, @NotNull BlockPosition blockPosition,
			@Nullable Data data) {
		return false;
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
