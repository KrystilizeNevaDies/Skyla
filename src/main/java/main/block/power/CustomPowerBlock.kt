package main.block.power

import main.block.SkylaCustomBlock
import main.block.power.powerblocks.PowerDiverterBlock
import net.kyori.adventure.text.Component
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.entity.Player.Hand
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.chunk.ChunkUtils
import net.minestom.server.utils.time.TimeUnit
import net.minestom.server.utils.time.UpdateOption
import java.util.*

abstract class CustomPowerBlock(defaultBlockStateId: Short, identifier: String, maxPower: Double) :
    SkylaCustomBlock(defaultBlockStateId, identifier) {
    constructor(block: Block, identifier: String, maxPower: Double) : this(block.blockId, identifier, maxPower)

    override fun onPlace(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val system = PowerSystem.of(instance)
        val chunkX = ChunkUtils.getChunkCoordinate(blockPosition.x.toDouble())
        val chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.z.toDouble())
        val chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ)
        val pos = ChunkUtils.getBlockIndex(blockPosition.x, blockPosition.y, blockPosition.z)
        system.registerBlock(chunk, pos, PowerBlock.of(blockPosition, data ?: return, this.javaClass))
    }

    override fun onDestroy(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val system = PowerSystem.of(instance)
        val chunkX = ChunkUtils.getChunkCoordinate(blockPosition.x.toDouble())
        val chunkZ = ChunkUtils.getChunkCoordinate(blockPosition.z.toDouble())
        val chunk = ChunkUtils.getChunkIndex(chunkX, chunkZ)
        val pos = ChunkUtils.getBlockIndex(blockPosition.x, blockPosition.y, blockPosition.z)
        system.unregisterBlock(chunk, pos, PowerBlock.of(blockPosition, data ?: return, this.javaClass))
    }

    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val system = PowerSystem.of(instance)
        val posX = blockPosition.x
        val posY = blockPosition.y
        val posZ = blockPosition.z
        val block = PowerBlock.of(blockPosition, data ?: return, this.javaClass)
        for (offset in listOf(-1, 1)) {
            val positions = arrayOf(
                BlockPosition(posX + offset, posY, posZ),
                BlockPosition(posX, posY + offset, posZ),
                BlockPosition(posX, posY, posZ + offset)
            )
            val blockSet: MutableSet<PowerBlock> = HashSet()
            for (adjacentBlockPosition in positions) {
                val adjacentBlock = system.getPowerBlock(adjacentBlockPosition)
                if (adjacentBlock != null && shouldSendPower(
                        blockPosition,
                        data,
                        adjacentBlockPosition,
                        adjacentBlock
                    )
                ) {
                    blockSet.add(adjacentBlock)
                }
            }
            handleInteraction(block, data, blockSet)
        }
    }

    protected fun handleInteraction(aBlock: PowerBlock, aBlockData: Data?, blockSet: Set<PowerBlock>) {
        val blockPower = aBlock.power
        val size = (blockSet.size + 1).toDouble()
        val averagePower = blockPower / size
        var leftOverPower = 0.0
        for (bBlock in blockSet) {
            val newBlockPower = bBlock.power + averagePower
            if (bBlock.maxPower < newBlockPower) {
                val residual = newBlockPower - bBlock.maxPower
                bBlock.power = bBlock.maxPower
                leftOverPower += residual
            } else {
                bBlock.power = newBlockPower
            }
        }
        aBlock.power = averagePower + leftOverPower
    }

    open fun shouldSendPower(
        sendingPosition: BlockPosition?,
        sendingBlock: Data?,
        receivingPosition: BlockPosition?,
        receivingBlock: PowerBlock
    ): Boolean {
        return receivingBlock.blockClass != PowerDiverterBlock::class.java
    }

    override fun onInteract(
        player: Player, hand: Hand, blockPosition: BlockPosition,
        data: Data?
    ): Boolean {
        if (player.isSneaking) return false
        val block = PowerBlock.of(blockPosition, data ?: return false, this.javaClass)
        player.sendActionBar(Component.text("Power: " + block.power))
        return true
    }

    override fun getUpdateOption(): UpdateOption? {
        return UpdateOption(5, TimeUnit.TICK)
    }

    init {
        PowerBlock.registerPowerBlock(this.javaClass, maxPower)
    }
}