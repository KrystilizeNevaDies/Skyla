package main.block.power.powerblocks

import main.block.BlockPropertyList
import main.block.power.CustomPowerBlock
import main.block.power.PowerBlock
import net.minestom.server.data.Data
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition
import kotlin.math.floor

object WireBlock : CustomPowerBlock(Block.REDSTONE_WIRE, "wire", 50.0) {
    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        super.update(instance, blockPosition, data)
        val block = PowerBlock.of(blockPosition, data ?: return, WireBlock::class.java)
        val blockStateID = instance.getBlockStateId(blockPosition)
        val newBlockStateID = withPower(block.power / block.maxPower)
        if (blockStateID != newBlockStateID) {
            instance.refreshBlockStateId(blockPosition, newBlockStateID)
        }
    }

    private fun withPower(power: Double): Short {
        val powerLevel = floor(power * 15).toInt()
        return baseBlockState
            .with("east", "side")
            .with("north", "side")
            .with("power", powerLevel.toString())
            .with("south", "side")
            .with("west", "side")
            .blockId
    }

    override fun blockStateOnPlacement(player: Player, position: BlockPosition, data: Data): Short {
        return baseBlockState
            .with("east", "side")
            .with("north", "side")
            .with("power", "0")
            .with("south", "side")
            .with("west", "side")
            .blockId
    }

    override fun createPropertyValues(): BlockPropertyList {
        return BlockPropertyList()
            .property("east", "none", "side", "up")
            .property("north", "none", "side", "up")
            .intRange("power", 0, 15)
            .property("south", "none", "side", "up")
            .property("west", "none", "side", "up")
    }
}