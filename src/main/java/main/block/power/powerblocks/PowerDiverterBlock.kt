package main.block.power.powerblocks

import main.block.BlockPropertyList
import main.block.power.CustomPowerBlock
import main.block.power.PowerBlock
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockFace
import net.minestom.server.utils.BlockPosition
import net.minestom.server.utils.Direction
import net.minestom.server.utils.MathUtils

object PowerDiverterBlock : CustomPowerBlock(Block.REPEATER, "power_diverter", 0.0) {
    override fun shouldSendPower(
        sendingPosition: BlockPosition?,
        sendingBlock: Data?,
        receivingPosition: BlockPosition?,
        receivingBlock: PowerBlock
    ): Boolean {
        return false
    }

    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        super.update(instance, blockPosition, data)

        // Move power from behind to in front
        val direction = data!!.get<Direction>(DIRECTION_KEY)
        val acceptingBlockFace = getBlockFace(direction)
        val aBlockPosition = blockPosition.getRelative(acceptingBlockFace)
        val bBlockPosition = blockPosition.getRelative(acceptingBlockFace!!.oppositeFace)
        val aBlock = instance.getCustomBlock(aBlockPosition)
        val bBlock = instance.getCustomBlock(bBlockPosition)
        if (aBlock is CustomPowerBlock && bBlock is CustomPowerBlock) {
            val aBlockData = instance.getBlockData(aBlockPosition)
            val bBlockData = instance.getBlockData(bBlockPosition)
            val aPowerBlock = PowerBlock.of(aBlockPosition, aBlockData ?: return, aBlock.javaClass)
            val bPowerBlock = PowerBlock.of(bBlockPosition, bBlockData ?: return, bBlock.javaClass)
            val aBlockPower = aPowerBlock.power
            val bBlockPower = bPowerBlock.power
            val bBlockMaxPower = bPowerBlock.maxPower
            val newPower = aBlockPower + bBlockPower
            if (newPower > bBlockMaxPower) {
                bPowerBlock.power = bBlockMaxPower
                aPowerBlock.power = newPower - bBlockMaxPower
            } else {
                bPowerBlock.power = newPower
                aPowerBlock.power = 0.0
            }
        }
    }

    override fun blockStateOnPlacement(player: Player, position: BlockPosition, data: Data): Short {
        val direction = MathUtils.getHorizontalDirection(player.position.yaw).opposite()
        data.set(DIRECTION_KEY, direction)
        return baseBlockState
            .with("delay", "1")
            .with("facing", direction.name.lowercase())
            .with("locked", "false")
            .with("powered", "false")
            .blockId
    }

    override fun createPropertyValues(): BlockPropertyList {
        return BlockPropertyList()
            .property("delay", "1", "2", "3", "4")
            .facingProperty("facing")
            .booleanProperty("locked")
            .booleanProperty("powered")
    }

    private const val DIRECTION_KEY = "direction"
    private fun getBlockFace(dir: Direction?): BlockFace? {
        return when (dir) {
            Direction.EAST -> BlockFace.EAST
            Direction.NORTH -> BlockFace.NORTH
            Direction.SOUTH -> BlockFace.SOUTH
            Direction.WEST -> BlockFace.WEST
            else -> {
                Throwable("Invalid direction for power diverter: $dir").printStackTrace()
                null
            }
        }
    }

}