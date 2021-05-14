package main

import net.minestom.server.MinecraftServer
import net.minestom.server.event.player.PlayerLoginEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.ChunkGenerator
import net.minestom.server.instance.ChunkPopulator
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.batch.ChunkBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.Position
import net.minestom.server.world.biomes.Biome
import java.util.*

fun main(args: Array<String>) {
    val server = MinecraftServer.init()
    TestServer.INSTANCE = MinecraftServer.getInstanceManager().createInstanceContainer()
    TestServer.INSTANCE!!.chunkGenerator = TestServer.TestGenerator()
    val handler = MinecraftServer.getGlobalEventHandler()

    handler.addEventCallback(PlayerLoginEvent::class.java) { event: PlayerLoginEvent ->
        event.setSpawningInstance(
            TestServer.INSTANCE!!
        )
    }

    handler.addEventCallback(PlayerSpawnEvent::class.java) { event: PlayerSpawnEvent ->
        val player = event.player
        player.teleport(TestServer.SPAWN_POSITION)
        val inventory = player.inventory
        inventory.addItemStack(ItemStack.of(Material.REDSTONE, 100))
        inventory.addItemStack(ItemStack.of(Material.REDSTONE_BLOCK, 100))
        inventory.addItemStack(ItemStack.of(Material.REPEATER, 100))
        inventory.addItemStack(ItemStack.of(Material.BELL, 100))
        inventory.addItemStack(ItemStack.of(Material.PISTON, 100))
    }

    SkylaExtension.runStandalone()
    server.start("0.0.0.0", 25565)
}

object TestServer {
    internal var INSTANCE: InstanceContainer? = null
    internal val SPAWN_POSITION = Position(.0, 100.0, .0)

    internal class TestGenerator : ChunkGenerator {
        override fun generateChunkData(batch: ChunkBatch, chunkX: Int, chunkZ: Int) {
            for (x in 0 until Chunk.CHUNK_SIZE_X) for (z in 0 until Chunk.CHUNK_SIZE_Z) {
                batch.setBlock(x, 80, z, Block.STONE)
            }
        }

        override fun fillBiomes(biomes: Array<Biome>, chunkX: Int, chunkZ: Int) {
            Arrays.fill(biomes, Biome.PLAINS)
        }

        override fun getPopulators(): List<ChunkPopulator>? {
            return null
        }
    }
}