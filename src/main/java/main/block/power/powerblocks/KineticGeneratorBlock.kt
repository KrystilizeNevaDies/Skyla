package main.block.power.powerblocks

import main.block.power.CustomPowerBlock
import main.block.power.PowerBlock
import net.minestom.server.data.Data
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.utils.BlockPosition

object KineticGeneratorBlock : CustomPowerBlock(Block.PISTON, "kinetic_generator", 20.0) {
    override fun update(instance: Instance, blockPosition: BlockPosition, data: Data?) {
        val block = PowerBlock.of(blockPosition, data ?: return, this.javaClass)
        block.power = (block.power + 2.5).coerceAtMost(block.maxPower)
        super.update(instance, blockPosition, data)
    }
}