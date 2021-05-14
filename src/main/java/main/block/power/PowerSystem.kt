package main.block.power

import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import main.SkylaExtension
import net.minestom.server.instance.Instance
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.chunk.ChunkUtils
import java.util.*

// Defines the behavior of the custom power system for one instance
class PowerSystem private constructor(val instance: Instance) {
    private val powerGrid: Long2ObjectMap<Array<PowerBlock?>> = Long2ObjectOpenHashMap()
    fun setPowerBlock(chunk: Long, pos: Int, block: PowerBlock?) {
        indexWireGrid(chunk)[pos] = block
    }

    fun getPowerBlock(chunk: Long, pos: Int): PowerBlock? {
        return indexWireGrid(chunk)[pos]
    }

    fun getPowerBlock(blockPosition: BlockPosition): PowerBlock? {
        val chunkX = ChunkUtils.getChunkCoordinate(blockPosition.x.toDouble())
        val chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.z.toDouble())
        val chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ)
        val positionIndex = ChunkUtils.getBlockIndex(blockPosition.x, blockPosition.y, blockPosition.z)
        return indexWireGrid(chunk)[positionIndex]
    }

    private fun indexWireGrid(chunk: Long): Array<PowerBlock?> {
        var chunkGrid = powerGrid[chunk]
        if (chunkGrid != null) return chunkGrid
        SkylaExtension.LOGGER!!.info("Creating new power grid for chunk: $chunk")
        chunkGrid = arrayOfNulls(16 * 256 * 16)
        powerGrid[chunk] = chunkGrid
        return chunkGrid
    }

    /**
     * Registers a block
     */
    fun registerBlock(chunk: Long, pos: Int, block: PowerBlock?) {
        indexWireGrid(chunk)[pos] = block
    }

    /**
     * Unregisters a block
     */
    fun unregisterBlock(chunk: Long, pos: Int, block: PowerBlock?) {
        indexWireGrid(chunk)[pos] = null
    }

    companion object {
        private val powerSystems: MutableMap<Instance, PowerSystem> = HashMap()
        fun of(instance: Instance): PowerSystem {
            if (powerSystems.containsKey(instance)) return powerSystems[instance]!!
            val powerSystem = PowerSystem(instance)
            powerSystems[instance] = powerSystem
            return powerSystem
        }
    }
}