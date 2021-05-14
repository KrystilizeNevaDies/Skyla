package main;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.Position;
import net.minestom.server.world.biomes.Biome;

public class TestServer {
	
	private static InstanceContainer INSTANCE;
	private static final Position SPAWN_POSITION = new Position(0, 100, 0);
	
    public static void main(String[] args) {
    	MinecraftServer server = MinecraftServer.init();
    	
    	INSTANCE = MinecraftServer.getInstanceManager().createInstanceContainer();

    	INSTANCE.setChunkGenerator(new TestGenerator());
    	
    	GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
    	
    	handler.addEventCallback(PlayerLoginEvent.class, (event) -> event.setSpawningInstance(INSTANCE));
    	handler.addEventCallback(PlayerSpawnEvent.class, (event) -> {
    		
    		Player player = event.getPlayer();
    		
    		player.teleport(SPAWN_POSITION);
    		
    		PlayerInventory inventory = player.getInventory();
    		
    		inventory.addItemStack(ItemStack.of(Material.REDSTONE).withAmount(100));
    		inventory.addItemStack(ItemStack.of(Material.REDSTONE_BLOCK).withAmount(100));
    		inventory.addItemStack(ItemStack.of(Material.REPEATER).withAmount(100));
    		inventory.addItemStack(ItemStack.of(Material.BELL).withAmount(100));
    		inventory.addItemStack(ItemStack.of(Material.PISTON).withAmount(100));
    	});
    	
    	SkylaExtension.runStandalone();
    	
    	server.start("0.0.0.0", 25565);
    }
    
    private static class TestGenerator implements ChunkGenerator {
		@Override
		public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
			for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++)
				for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
					batch.setBlock(x, 80, z, Block.STONE);
				}
		}

		@Override
		public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
			Arrays.fill(biomes, Biome.PLAINS);
		}

		@Override
		public @Nullable List<ChunkPopulator> getPopulators() {
			return null;
		}
    }
}
